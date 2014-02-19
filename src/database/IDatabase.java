package database;

import java.io.IOException;
import java.util.List;

import model.Song;

public interface IDatabase {
    
    /**
     * Inserts song into db... just to define db structure
     * @param song
     * @return boolean value depending on if insert is sucessfull
     */
     public boolean insertSong(Song song);
    
    /**
     * Retrieves every document from collection and creates list of Song objects
     * @throws IOException 
     * @return List List of song objects in database collection
     **/
    public List<Song> retrieveSongs() throws IOException;
}
