package model.translator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import model.Song;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;

public enum Translator  {
    INSTANCE;
    
    
    
    /**
     * Loads a MIDI file and call the constructor of song object
     * on load success. The MIDI file is copied to the program so that it
     * can be accessible again
     * 
     * @param path, relative path to MIDI file
     */
    public Song loadMidiToSong(String path) throws IOException {
        Score score = new Score();
        final String destPath = "res/"+score.hashCode()+".midi";
        FileUtils.copyFile(new File(path), new File(destPath));
        Read.midi(score, destPath);
        
        // using path as title for the time being. 
        return new Song(score, path, destPath);
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
    
    
    public void playPart(Song song, int partIndex) {
        Play.midi(new Score(song.getPart(partIndex), "part " + partIndex, song.getTempo()));
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