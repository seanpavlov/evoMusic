package translator;

import jm.music.data.Score;
import jm.util.Read;
import structure.Song;

public class Translator implements ITranslator {
    private static Translator instance = null;
    
    /**
     * Private constructor for singleton pattern
     */
    private Translator() {}
    

    public static Translator getInstance() {
       if(instance == null) {
          instance = new Translator();
       }
       return instance;
    }
    
    
    public Song loadMidiToSong(String path){
        Score score = new Score("score");
        Read.midi(score, path);
        
        return new Song(score);
    }
    

    public void saveSongToMidi(String path, String name, Song song){
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