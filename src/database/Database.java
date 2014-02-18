package database;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import model.Song;
import model.translator.Translator;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import enumerators.TrackTag;

public class Database implements IDatabase{
    
    private static Database instance = null;
    private MongoClient mongoClient;
    private DB db;
    private DBCollection songs;
    private final static String TITLE_KEY = "title"
            , TRACK_REF_KEY = "track_ref"
            , MIDI_PATH_KEY = "midi_path"
            , USER_TAGS_KEY = "user_tags"
            , DB_NAME = "evoMusic"
            , SONG_COLLECTION = "Songs";
    
    // Song collection:
    // title,
    // track [ 
    // {index, tag}
    // ,...]
    // midi-path
    // user tags ex. "cool jazz"

    
    protected Database(){
        try {
            mongoClient = new MongoClient();
            db = this.mongoClient.getDB(DB_NAME);
            songs = db.getCollection(SONG_COLLECTION);
        } catch (UnknownHostException e) {
            //Take care of Exception TODO
            e.printStackTrace();
        }
    }
    
    public static Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    @Override
    public DBObject createDBObject(Song song) {
        //TODO implement method
        return null;
    }

    @Override
    public Song createSongObject(BasicDBObject dbDoc) throws IOException {
        String songPath = (String)dbDoc.get(MIDI_PATH_KEY);
        Song song = Translator.INSTANCE.loadMidiToSong(songPath);
        return song;
    }

    @Override
    public void saveDbObject(DBObject dbDoc) { 
        //TODO implement method
    }

    @Override
    public DBObject retreiveDBObject() {
        //TODO implement method
        return null;
    }

    /**
     * Inserts song into db... just to define db structure
     * @param song
     * @return WriteResult returned by mongo on insertion
     */
    public WriteResult insertSong(Song song) {
        final int nbrOfTracks = song.getNbrOfTracks();
        TrackTag[] tracks = new TrackTag[nbrOfTracks];
        for (int i = 0; i < nbrOfTracks; i++) {
            tracks[i] = song.getTrackTag(i);
        }
        
        DBObject songData = new BasicDBObject(TITLE_KEY, song.getTitle())
//                    .append(TRACK_REF_KEY, tracks) TODO: SERIALIZE ENUMS
                    .append(MIDI_PATH_KEY, song.getPath())
                    .append(USER_TAGS_KEY, song.getUserTags());
        return songs.insert(songData);
    }

    @Override
    public List<Song> retrieveSongs() throws IOException {
        List<Song> listOfSongs = new LinkedList<Song>();
        DBCursor cursor = songs.find();
        
        try {
            while(cursor.hasNext()) {
                listOfSongs.add(this.createSongObject((BasicDBObject) cursor.next()));
            }
         } finally {
            cursor.close();
         }
        
        return listOfSongs;
    }
    
}
