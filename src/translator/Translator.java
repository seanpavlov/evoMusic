package translator;

import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;
import structure.Song;

public enum Translator implements ITranslator {
    INSTANCE;
    
    
    @Override
    public Song loadMidiToSong(String path){
        Score score = new Score();
        Read.midi(score, path);
        
        return new Song(score, path);
    }
    
    
    @Override
    public void saveSongToMidi(String path, String name, Song song){
        //must know more about object
        
    }


    /**
     * Play a song object in JMusics built in player
     * 
     * @param song to be played
     */
    public void playSong(Song song){
        Play.midi(song);
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