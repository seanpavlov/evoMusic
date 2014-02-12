package translator;

import jm.music.data.Score;
import jm.util.Read;
import structure.Song;

import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;

public class Translator {
    private static Translator instance = null;
    
    /**
     * Private constructor for singleton pattern
     */
    private Translator() {}
    
    /**
     * Use getInstance() to access Translator
     * 
     * @return static Translator
     */
    public static Translator getInstance() {
       if(instance == null) {
          instance = new Translator();
       }
       return instance;
    }
    
    /**
     * Loads a MIDI file and call the constructor of song object
     * on load success
     * 
     * @param path, relative path to MIDI file
     */
    public void loadMIDIToSong(String path){
        Score sc = new Score("sc");
        Read.midi(sc, path);
        
        //calls constructor here
    }
    
    /**
     * Function takes a path to save the MIDI file to, and the object which 
     * to save and saves it.
     * 
     * @param path, where to save
     * @param name, name of the file
     * @param song, song object to unload
     * 
     */
    public void saveSongToMIDI(String path, String name, Song song){
        //must know more about object
        
    }
    /**
     * Play a song object in JMusics built in player
     * 
     * @param song to be played
     */
    public void playSong(Song song){
        //Play.midi(song.getScore());
    }

    /**
     * Show the structure of the song in JMusics built in MIDI display
     * 
     * @param song to be played
     */
    public void showSong(Song song){
        //Play.midi(song.getScore());
    }
}