package main;

import model.Song;
import model.translator.Translator;
import crossover.Crossover;

public class Main {

    public static void main(String[] args) {
        // String megamanPath = "midifiles/mm2wily1.mid";
        // Song testSong = Translator.INSTANCE.loadMidiToSong(megamanPath);
        // Translator.INSTANCE.playSong(testSong);
        Song sweden = Translator.INSTANCE
                .loadMidiToSong("midifiles/Sweden.mid");
        Song norway = Translator.INSTANCE
                .loadMidiToSong("midifiles/Norway.mid");
        Crossover crossover = new Crossover(new Song[] { sweden, norway });
        crossover.setNumberOfIntersections(8);
        Translator.INSTANCE.playPart(crossover.crossMutate(), 0);
    }

}
