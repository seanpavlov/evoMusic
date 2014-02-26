package com.evoMusic.database;

import java.io.IOException;
import java.util.List;

import com.evoMusic.model.Song;

public interface IDatabase {
    
    /**
     * Inserts song into db... just to define db structure
     * @param song
     * @return boolean value depending on if insert is successful
     */
     public boolean insertSong(Song song);
    
    /**
     * Retrieves every document from collection and creates list of Song objects
     * @throws IOException 
     * @return List List of song objects in database collection
     **/
    public List<Song> retrieveSongs() throws IOException;
    
    
    /**
     * Removes specific song in database
     * @param song Song object to be removed from database
     * @return Boolean value depending on if remove is successful
     * */
    public boolean removeSong(Song song);
    
    /**
     * Updates specific song in database
     * @param oldSong Song object to be update 
     * @param newSong Song object to update with
     * @return Boolean value depending on if update is successful 
     * */
    public boolean updateSong(Song oldSong, Song newSong);
    
    /**
     * Drop database if exists
     * @param dbName 
     *          name of the database to drop
     */
    public void dropDb(String dbName);
    
}
