package com.evoMusic.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bson.types.ObjectId;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.util.TrackTag;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;

public class MongoDatabase implements IDatabase {

    private Logger dbLogger = null;
    private static MongoDatabase instance = null;
    private MongoClient mongoClient;
    private DB db;
    private DBCollection songs;
    private String dbResFolder;
    public final static String TITLE_KEY = "title",
            TRACK_REF_KEY = "track_ref", MIDI_PATH_KEY = "midi_path",
            USER_TAGS_KEY = "user_tags", DB_NAME = "evoMusic",
            SONG_COLLECTION = "Songs", ID_KEY = "_id", 
            DEFAULT_DB_RES_FOLDER = "dbres";

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
            setDbResfolder(DEFAULT_DB_RES_FOLDER);

            // make sure database is writable an more importantly that it's
            // reachable.
            if (mongoClient.isLocked()) {
                mongoClient.unlock();
            }
        } catch (IOException e) {
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
     * Set the folder used for db resources. ie song midi files
     * @param dbResFolder the folder from which to save and load data
     */
    public void setDbResfolder(String dbResFolder) {
        this.dbResFolder = dbResFolder;
    }
    
    /**
     * Creates database representation from given Song object
     * 
     * @param song
     *            Song object to be saved to the database
     * @return DBObject mongo database object representing the song
     * */
    private DBObject createDBObject(Song song) {
        
        String path = Translator.INSTANCE.saveSongToMidi(song, song.getTitle(), dbResFolder);

        return new BasicDBObject("_id", song.getDbRef()).append(TITLE_KEY, song.getTitle())
                .append(TRACK_REF_KEY, createTrackTagDBList(song))
                .append(MIDI_PATH_KEY, path)
                .append(USER_TAGS_KEY, song.getUserTags());
    }
    
    /**
     * 
     * @param song holding the tracks
     * @return mongo database representation of the list
     */
    private BasicDBList createTrackTagDBList(Song song) {
        final int nbrOfTracks = song.getNbrOfTracks();
        final BasicDBList tracks = new BasicDBList();
        // array index is the same as track index
        // the tracks that are not used are stores as well
        for (int i = 0; i < nbrOfTracks; i++) {
            //Build tag list for track
            BasicDBList tags = new BasicDBList();
            for(TrackTag tag : song.getTrackTags(i)){
                tags.add(DBEnum.of(tag));
                
            }
            tracks.add(tags);
        }
        return tracks;
    }

    /**
     * Creates Song object from database representation
     * 
     * @param dbDoc
     *            Database representation of a Song Object
     * @return Song song object from database
     * */
    private Song createSongObject(BasicDBObject dbDoc) {
        final String songPath = dbDoc.getString(MIDI_PATH_KEY);
        if (Files.notExists(Paths.get(songPath))) {
            return null;
        }
        Song song;
        song = Translator.INSTANCE.loadMidiToSong(songPath);
        
        song.setDbRef((ObjectId)dbDoc.get("_id"));

        //Mongo basicdblist containing lists of tracktags
        final BasicDBList mongoTrackTags = ((BasicDBList) dbDoc
                .get(TRACK_REF_KEY));
        //Track index 
        int i = 0;
        for (Iterator<Object> trackTagIt = mongoTrackTags.iterator(); trackTagIt
                .hasNext(); i++) {
            //Mongo basicdblist containing tracktags for track
            final BasicDBList trackTags = (BasicDBList) trackTagIt.next();
            //Add every tracktag to trackindex i in song
            for(Object enumT : trackTags){
                song.addTagToTrack(i, DBEnum.to(TrackTag.class, enumT));
            }
        }
        // set all user tags
        final List<String> userTags = new ArrayList<String>();
        userTags.addAll(Arrays.asList(((BasicDBList) dbDoc.get(USER_TAGS_KEY))
                .toArray(new String[0])));

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
        LinkedList<Song> listOfSongs = new LinkedList<Song>();
        DBCursor cursor = songs.find();
        while (cursor.hasNext()) {
            BasicDBObject songDb = (BasicDBObject) cursor.next();
            Song song = createSongObject(songDb);
            if (song != null) {
                listOfSongs.add(song);
            }
        }
        cursor.close();
        return listOfSongs;
    }
    
    public Map<ObjectId, String> getBrokenPaths() {
        List<Song> workingSongs = retrieveSongs();
        List<ObjectId> songIds = new LinkedList<ObjectId>();
        for (Song s : workingSongs) {
            System.out.println("yello: SONG LOLOL" + s);
            songIds.add(s.getDbRef());
        }
        DBCursor cursor = songs.find(QueryBuilder.start(ID_KEY).notIn(songIds).get());
        Map<ObjectId, String> result = new HashMap<ObjectId, String>();
        for (DBObject dbo : cursor) {
            result.put((ObjectId)dbo.get(ID_KEY), (String)dbo.get(MIDI_PATH_KEY));
        }
        return result;
    }
    
    @Override
    public boolean removeSong(Song song) {
        return removeSong(song.getDbRef());
    }
    
    @Override
    public boolean removeSong(ObjectId dbRef) { 
        DBObject dbo = null;
        try {
            dbo = songs.findAndRemove(new BasicDBObject("_id", dbRef));
        } catch (MongoException me) {
            dbLogger.log(Level.WARNING, "Remove Song", me);
            return false;
        }
        return dbo != null;
       
    }
    
    @Override
    public boolean updateSong(Song oldSong, Song newSong) {
        try {
            songs.findAndModify(
                    new BasicDBObject(TITLE_KEY, oldSong.getTitle())
                    .append(TRACK_REF_KEY, createTrackTagDBList(oldSong))
                    .append(USER_TAGS_KEY, oldSong.getUserTags()),

                    new BasicDBObject("$set", new BasicDBObject(TITLE_KEY, newSong.getTitle())
                    .append(TRACK_REF_KEY, createTrackTagDBList(newSong))
                    .append(USER_TAGS_KEY, newSong.getUserTags())));
            
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

    public Song getSong(ObjectId songRef) {
        BasicDBObject songDoc = (BasicDBObject)songs.findOne(songRef);
        Song song = createSongObject(songDoc);
        return song;
    }

}
