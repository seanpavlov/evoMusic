package main;

import model.Song;
import model.translator.Translator;
import jm.JMC;

public class Main implements JMC {
            
    public static void main(String[] args) {
    	String megamanPath = "midifiles/mm2wily1.mid";
    	Song testSong = Translator.INSTANCE.loadMidiToSong(megamanPath);
    	Translator.INSTANCE.playSong(testSong);
    }

}
