package com.evoMusic.model;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.evoMusic.model.enumerators.TrackTag;

import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;
import jm.util.View;
import jm.util.Write;

public enum Translator  {
    INSTANCE;
    
    /**
     * Loads a MIDI file and call the constructor of song object
     * on load success. 
     * 
     * @param path, relative path to MIDI file
     */
    public Song loadMidiToSong(String path) throws IOException {
        //Remove appendix
        String name = path.split("\\.")[0];
        int pathStructure = name.lastIndexOf("/");
        //If '/' does exist in filename, meaning its a path, substring from last appearance
        if(pathStructure != -1)
             name = name.substring(++pathStructure);
        Score score = new Score(name);
        Read.midi(score, path);
        
        return new Song(score);
    }
    
    /**
     * Loads a MIDI file and call the constructor of song object once every track 
     * is tagged, the none tag is used for all irrelevant tracks
     * 
     * @param path to load from
     * @return new song object
     */
    public Song loadMidiToSongAndTagTracks(String path){
        Scanner sc = new Scanner(System.in);
        int i = 0;
        Score score = new Score();
        Read.midi(score, path);
        Song song = new Song(score);
        System.out.println("Please tag these tracks corresponding tracktag, use none for irrelevant tracks");
        
        for (Part p : song.getScore().getPartArray()){
            Translator.INSTANCE.showPart(p);
            String tag = sc.next().toString().toUpperCase();
            
            while (!TrackTag.contains(tag)){
                System.out.println("No such TrackTag, enter again");
                tag = sc.next();
            }
            
            TrackTag t = TrackTag.valueOf(tag);
            song.addTagToTrack(p, t);
            i++;
        }
        
        return song;
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