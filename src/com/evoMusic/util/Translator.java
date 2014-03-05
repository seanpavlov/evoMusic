package com.evoMusic.util;
import java.io.File;
import java.io.IOException;

import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;
import jm.util.View;
import jm.util.Write;

import com.evoMusic.model.Song;

public enum Translator  {
    INSTANCE;
    
    /**
     * Loads a MIDI file and call the constructor of song object
     * on load success. 
     * 
     * @param path, relative path to MIDI file
     * @return song, if a new song object or null if file was not found
     * @throws IOException 
     */
    public Song loadMidiToSong(String path) {

        // split "./something/etc/file.midi"
        final String[] splitPath = path.split("/");
        // last item is "file.midi" and then "file"
        final String name = splitPath[splitPath.length-1].split("\\.")[0];
        
        final File midiFile = new File(path);
        if(!midiFile.exists()) {
            return null;
        }
        
        // Using this method to avoid some annoying printing by jMusic.
        // It still prints "Convert SMF to JM". It appears to be hardcoded
        final Score score = Read.midiOrJmWithNoMessaging(midiFile);
        score.setTitle(name);
        return new Song(score);
    }
    

    /**
     * Saves a song the default location. 
     * 
     * @param song, song object to save
     * @return the path to the saved MIDI file
     */  
    public String saveSongToMidi(Song song, String name) {
        File theDir = new File("./output/");
        if (!theDir.exists()){
            theDir.mkdir();
        }
        
        int copy = 0;
        File outputFile = null;
        String path = "";
        do {
            path = "./output/" + name + (copy != 0 ? "-"+copy : "")+ ".mid";
            outputFile = new File(path); 
                // if dupe, filename is appended "-1"
            copy++;
        } while (outputFile.exists());
        Write.midi(song.getScore(), path);
        return path;
    }

    /**
     * Play a song object in JMusics built in player
     * 
     * @param song to be played
     */
    public void playSong(Song song){
        Play.midi(song.getScore());
    }
    
    /**
     * Play a single track of a song
     * 
     * @param song
     * @param trackIndex
     */
    public void playPart(Song song, int trackIndex) {
        Play.midi(new Score(song.getTrack(trackIndex), "part " + trackIndex, song.getTempo()));
    }

    
    /**
     * Show the structure of the song in JMusics built in MIDI display
     * 
     * @param song to be shown
     */
    public void showSong(Song song){
        View.show(song.getScore());
    }
    
    /**
     * Show the structure of the part in JMusics built in MIDI display
     * 
     * @param part to be shown
     */
    public void showPart(Part part){
        View.sketch(part);
    }
    
    /**
     * Closes all showing windows 
     */
    public void closeDisplayWindow(){
       // View.
    }
}