package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Song;
import mutation.ISubMutator;
import mutation.Mutator;
import mutation.ScaleOfFifthMutator;
import mutation.SimplifyMutator;
import translator.Translator;
import crossover.Crossover;

public class Main {

    public static void main(String[] args) throws IOException {
        Song super_mario_bros_theme = Translator.INSTANCE
                .loadMidiToSong("midifiles/super_mario_bros_theme.mid");
        Song super_mario_world_overview  = Translator.INSTANCE
                .loadMidiToSong("midifiles/super_mario_world_overworld.mid");
        Song avicii_levels  = Translator.INSTANCE
                .loadMidiToSong("midifiles/avicii_levels.mid");
        List<ISubMutator> subMutators = new ArrayList<>();
        subMutators.add(new ScaleOfFifthMutator(0.5, 1));
        subMutators.add(new SimplifyMutator(0.3, 2, 1));
        Mutator m = new Mutator(subMutators, 0.5);
        m.mutate(super_mario_bros_theme);
        Translator.INSTANCE.playSong(super_mario_bros_theme);
//        Translator.INSTANCE.saveSongToMidi(mutatedSong, "hej");
//        Translator.INSTANCE.showSong(mutatedSong);
    }
}
