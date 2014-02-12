package translator;

import jm.music.*;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;

public class Translator {
    
    /**
     * Loads a MIDI file and call the constructor of song object
     * on load success
     * 
     * @param path, relative path to MIDI file
     */
    public void loadMidi(String path){
        Score sc = new Score("sc");
        Read.midi(sc, path);
        
        //calls constructor here
    }
 
    
    /**
     * Function takes a path to save the MIDI file to, and the object which 
     * to save and saves it.
     * 
     * @param path, where to save
     * @param song, song object to unload
     */
    public void saveMidi(String path, Object song){
        //must know more about object
        
    }
    
    public void play(Object song){
        //Play.midi(song.getScore());
          
    }
    
}
