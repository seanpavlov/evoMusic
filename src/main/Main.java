package main;

import java.io.IOException;

import model.Song;
import model.translator.Translator;
import mutation.Mutation;
import crossover.Crossover;

public class Main {

    public static void main(String[] args) throws IOException {
        // String megamanPath = "midifiles/mm2wily1.mid";
        // Song testSong = Translator.INSTANCE.loadMidiToSong(megamanPath);
        // Translator.INSTANCE.playSong(testSong);
        Song super_mario_bros_theme = Translator.INSTANCE
                .loadMidiToSong("midifiles/super_mario_bros_theme.mid");
        Song super_mario_world_overview  = Translator.INSTANCE
                .loadMidiToSong("midifiles/super_mario_world_overworld.mid");
        Crossover crossover = new Crossover(new Song[] { super_mario_bros_theme, super_mario_world_overview});
        crossover.setNumberOfIntersections(8);
        Song crossedSong = crossover.makeCrossover();
        Mutation m = new Mutation(0.2);
        Song mutatedSong = m.mutate(crossedSong);
        Translator.INSTANCE.playPart(mutatedSong, 0);
    }

}
