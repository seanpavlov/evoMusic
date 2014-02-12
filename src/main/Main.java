package main;

import jm.JMC;
import structure.Song;
import translator.Translator;

public class Main implements JMC {
            
    public static void main(String[] args) {
    	String megamanPath = "midifiles/mm2wily1.mid";
    	Song testSong = Translator.INSTANCE.loadMidiToSong(megamanPath);
    	Translator.INSTANCE.playSong(testSong);
    }

}
