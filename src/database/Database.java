package database;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import model.Song;
import translator.Translator;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

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

    /**
     * Creates database representation from given Song object
     * 
     * @param song, Song object to be saved to the database
     * */
    public DBObject createDBObject(Song song) {
        final int nbrOfTracks = song.getNbrOfTracks();
        
        // array index is the same as track index
        // the tracks that are not used are stores as well
        final String[] tracks = new String[nbrOfTracks];
        for (int i = 0; i < nbrOfTracks; i++) {
            tracks[i] = song.getTrackTag(i).dbName;
        }
        
        return new BasicDBObject(TITLE_KEY, song.getTitle())
                    .append(TRACK_REF_KEY, tracks)
                    .append(MIDI_PATH_KEY, Translator.INSTANCE.saveSongToMidi(song, song.getTitle()))
                    .append(USER_TAGS_KEY, song.getUserTags());
    }

    /**
     * Creates Song object from database representation
     * 
     * @param dbDoc, Database representation of a Song Object
     * */
    public Song createSongObject(BasicDBObject dbDoc) throws IOException {
        final String songPath = dbDoc.getString(MIDI_PATH_KEY);
        final Song song = Translator.INSTANCE.loadMidiToSong(songPath);
//        song.setTitle(dbDoc.getString(TITLE_KEY)); we use same title as in score
        song.setTrackTags(new ArrayList<String>(Arrays.asList((String[]) dbDoc.get(TRACK_REF_KEY))));
        song.setUserTags(new ArrayList<String>(Arrays.asList((String[]) dbDoc.get(USER_TAGS_KEY))));
        return song;
    }

    @Override
    public boolean insertSong(Song song) {
        try{
            songs.insert(createDBObject(song));
        }catch(MongoException me){
            return false;
        }
        return true;
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
