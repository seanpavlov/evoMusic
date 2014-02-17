package main;

import model.Song;
import model.translator.Translator;


public class Main {
            
    public static void main(String[] args) {
    	String megamanPath = "midifiles/mm2wily1.mid";
    	Song testSong = Translator.INSTANCE.loadMidiToSong(megamanPath);
    	Translator.INSTANCE.playSong(testSong);
    }

}
