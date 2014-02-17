package db;

import java.net.UnknownHostException;

import model.Song;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import enumerators.TrackTag;

public class MongoAdapter {

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
    // track ->
    // index
    // tag ex. "rhythm"
    // midi-path
    // user tags ex. "cool jazz"

    
    /// i just added this to get started with structure.. not thought through..
    public MongoAdapter() throws UnknownHostException {
        mongoClient = new MongoClient();
        db = mongoClient.getDB(DB_NAME);
        songs = db.getCollection(SONG_COLLECTION);
    }
    
    /**
     * Insers song into db... just to define db structure
     * @param song
     * @return
     */
    public WriteResult insertSong(Song song) {
        TrackTag[] tracks = new TrackTag[song.getPartArray().length];
        for (int i = 0; i < song.getPartArray().length; i++) {
            tracks[i] = song.getTrackTag(i);
        }
        
        BasicDBObject songData = new BasicDBObject(TITLE_KEY, song.getTitle())
//                    .append(TRACK_REF_KEY, tracks) TODO: SERIALIZE ENUMS
                    .append(MIDI_PATH_KEY, song.getPath())
                    .append(USER_TAGS_KEY, song.getUserTags());
        return songs.insert(songData);
    }
}