package translator;
import java.io.File;
import java.io.IOException;

import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;
import jm.util.View;
import jm.util.Write;
import model.Song;

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
        
        int copy = 0;
        File outputFile = null;
        String path = "";
        do {
            path = "./output/" + name + (copy != 0 ? "-"+copy : "")+ ".midi";
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
     * @param song to be played
     */
    public void showSong(Song song){
        View.show(song.getScore());
    }
}