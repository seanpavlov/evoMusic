package database;

import java.io.IOException;
import java.util.List;
import model.Song;

public interface IDatabase {
    
    
    /*
     * Creates database representation from given Song object
     * 
     * @param song, Song object to be saved to the database
     * */
    public Object createDBObject(Song song);
    
    /*
     * Creates Song object from database representation
     * 
     * @param dbDoc, Database representation of a Song Object
     * */
    public Song createSongObject(Object dbDoc) throws IOException;
    
    /*
     * Saves database object to database
     * 
     * @param dbDoc, Database object to be save to database
     * */
    
    public void saveDbObject(Object dbDoc);
    
    
    /*
     * Retrieves database object from database for given parameters
     * 
     * @param TODO
     * */
    public Object retreiveDBObject();
    
    /*
     * Retrieves every document from collection and creates list of Song objects
     * @throws IOException 
     * 
     * */
    public List<Song> retrieveSongs() throws IOException;
}
