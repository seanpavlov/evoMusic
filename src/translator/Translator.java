package translator;
import java.io.File;
import java.io.IOException;

import model.Song;
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
        Score score = new Score();
        Read.midi(score, path);
        
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
        
        String path = "./output/" + name + ".midi";
        File f = new File(path);
        
        if(f.exists() && !f.isDirectory()){
            int i = 1;
            while (f.exists()){
                path = "./output/" + name + "(" + i + ")" + ".midi";                       
                f = new File(path);
                i++;
            }
        }
        
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
     * @param song to be played
     */
    public void showSong(Song song){
        View.show(song.getScore());
    }
}