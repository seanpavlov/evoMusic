package database;

import com.mongodb.BasicDBObject;

import model.Song;

public interface IDatabase {
    
    
    /*
     * Creates database representation from given Song object
     * 
     * @param song, Song object to be saved to the database
     * */
    public BasicDBObject createDBObject(Song song);
    
    /*
     * Creates Song object from database representation
     * 
     * @param dbDoc, Database representation of a Song Object
     * */
    public Song createSongObject(BasicDBObject dbDoc);
    
    /*
     * Saves database object to database
     * 
     * @param dbDoc, Database object to be save to database
     * */
    
    public void saveDbObject(BasicDBObject dbDoc);
    
    
    /*
     * Retrieves database object from database for given parameters
     * 
     * @param TODO
     * */
    public BasicDBObject retreiveDBObject();
}
