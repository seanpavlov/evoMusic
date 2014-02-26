package com.evoMusic.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.enumerators.TrackTag;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class MongoDatabase implements IDatabase {

    private Logger dbLogger = null;
    private static MongoDatabase instance = null;
    private MongoClient mongoClient;
    private DB db;
    private DBCollection songs;
    public final static String TITLE_KEY = "title",
            TRACK_REF_KEY = "track_ref", MIDI_PATH_KEY = "midi_path",
            USER_TAGS_KEY = "user_tags", DB_NAME = "evoMusic",
            SONG_COLLECTION = "Songs";

    // Song collection:
    // title,
    // track [
    // {index, tag}
    // ,...]
    // midi-path
    // user tags ex. "cool jazz"

    protected MongoDatabase() {
        try {
            dbLogger = Logger.getLogger("dbLogger");
            FileHandler dbFileHandler = new FileHandler("DBLogFile.log", true);
            SimpleFormatter dbFormatter = new SimpleFormatter();
            dbFileHandler.setFormatter(dbFormatter);
            dbLogger.addHandler(dbFileHandler);
            dbLogger.setLevel(Level.WARNING);
            mongoClient = new MongoClient();
            useDbName(DB_NAME);

            // make sure database is writable an more importantly that it's
            // reachable.
            if (mongoClient.isLocked()) {
                mongoClient.unlock();
            }
        } catch (IOException e) {
            // TODO Take care of Exception(s)
            e.printStackTrace();
        }
    }

    public static MongoDatabase getInstance() {
        if (instance == null) {
            instance = new MongoDatabase();
        }
        return instance;
    }

    /**
     * Creates database representation from given Song object
     * 
     * @param song
     *            Song object to be saved to the database
     * */
    private DBObject createDBObject(Song song) {
        final int nbrOfTracks = song.getNbrOfTracks();

        // array index is the same as track index
        // the tracks that are not used are stores as well

        final LinkedList<DBEnum<TrackTag>> tracks = new LinkedList<DBEnum<TrackTag>>();

        for (int i = 0; i < nbrOfTracks; i++) {
            //tracks.add(DBEnum.of(song.getTrackTag(i)));
            // TODO Fix so that it can take multiple track tags.
        }

        return new BasicDBObject(TITLE_KEY, song.getTitle())
                .append(TRACK_REF_KEY, tracks)
                .append(MIDI_PATH_KEY,
                        Translator.INSTANCE.saveSongToMidi(song,
                                song.getTitle()))
                .append(USER_TAGS_KEY, song.getUserTags());
    }

    /**
     * Creates Song object from database representation
     * 
     * @param dbDoc
     *            Database representation of a Song Object
     * */
    private Song createSongObject(BasicDBObject dbDoc) throws IOException {
        final String songPath = dbDoc.getString(MIDI_PATH_KEY);
        final Song song = Translator.INSTANCE.loadMidiToSong(songPath);

        // set all track tags
        final List<TrackTag> trackTags = new ArrayList<TrackTag>();
        final BasicDBList mongoTrackTags = ((BasicDBList) dbDoc
                .get(TRACK_REF_KEY));

        for (Iterator<Object> trackTagIt = mongoTrackTags.iterator(); trackTagIt
                .hasNext();) {
            trackTags.add(DBEnum.to(TrackTag.class, trackTagIt.next()));
        }

        // set all user tags
        final List<String> userTags = new ArrayList<String>();
        userTags.addAll(Arrays.asList(((BasicDBList) dbDoc.get(USER_TAGS_KEY))
                .toArray(new String[0])));

        song.setTrackTags(trackTags);
        song.setUserTags(userTags);
        return song;
    }

    /**
     * Use a specific database. This is by default set to
     * {@link MongoDatabase#DB_NAME} but may be manually set for i.e. testing
     * purposes
     * 
     * @param dbName
     *            The new name to use.
     */
    public void useDbName(String dbName) {
        db = mongoClient.getDB(dbName);
        songs = db.getCollection(SONG_COLLECTION);
    }

    @Override
    public boolean insertSong(Song song) {
        try {
            songs.insert(createDBObject(song));
        } catch (MongoException me) {
            dbLogger.log(Level.WARNING, "Insert Song", me);
            return false;
        }
        return true;
    }

    @Override
    public List<Song> retrieveSongs() {
        List<Song> listOfSongs = new LinkedList<Song>();
        DBCursor cursor = songs.find();
        try {
            while (cursor.hasNext()) {
                listOfSongs.add(this.createSongObject((BasicDBObject) cursor
                        .next()));
            }
        } catch (IOException e) {
            System.err.println("WARN: unable to recreate instance from db: "
                    + e.getMessage());
        } finally {
            cursor.close();
        }

        return listOfSongs;
    }

    @Override
    public boolean removeSong(Song song) {
        try {
            songs.remove(createDBObject(song));
        } catch (MongoException me) {
            dbLogger.log(Level.WARNING, "Remove Song", me);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateSong(Song oldSong, Song newSong) {
        try {
            songs.update(createDBObject(oldSong), createDBObject(newSong));
        } catch (MongoException me) {
            dbLogger.log(Level.WARNING, "Update Song", me);
            return false;
        }
        return true;
    }

    @Override
    public void dropDb(String dbName) {
        mongoClient.dropDatabase(dbName);
    }
}
