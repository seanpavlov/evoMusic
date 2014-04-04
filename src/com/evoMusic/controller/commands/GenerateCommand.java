package com.evoMusic.controller.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.OctaveMutator;
<<<<<<< HEAD
import com.evoMusic.model.geneticAlgorithm.rating.BeatRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.CrazyNoteOctaveRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRepetionRater;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.NoSilenceRater;
=======
import com.evoMusic.model.geneticAlgorithm.mutation.RandomNoteMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ReverseMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ScaleOfFifthMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.SimplifyMutator;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRepetionRater;
>>>>>>> 320d955cecba46c76d1204180d138c733f972546
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.ScaleWhizz;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.Parameters;
import com.google.common.collect.Sets;

public class GenerateCommand extends AbstractCommand {

    private List<Song> selectedSongs;
    private Parameters c = Parameters.getInstance();
    
    /**
     * Creates the generate command
     * @param selectedSongs
     *              reference to the songs selected to be used for generating
     *              individuals
     */
    public GenerateCommand(List<Song> selectedSongs) {
        this.selectedSongs = selectedSongs;
    }
    
    @Override
    public boolean execute(String[] args) {
        if(selectedSongs.size() < 2) {
            return false;
        }
        int iterations = Integer.parseInt(args[0]);
        List<ISubMutator> allMut = new ArrayList<ISubMutator>();
<<<<<<< HEAD
//        allMut.add(new RandomNoteMutator(0.5, 12));
        allMut.add(new OctaveMutator(0.01, 6));
//        allMut.add(new ReverseMutator(0, 4, 4, true));
//        allMut.add(new ScaleOfFifthMutator(0, 3));
//        allMut.add(new SimplifyMutator(0, 4, 0.1));
        List<SubRater> subRaters = new LinkedList<SubRater>();
        subRaters.add(new MelodyRepetionRater(1));
        subRaters.add(new ScaleWhizz(1));
        subRaters.add(new BeatRepetitionRater(1));
        subRaters.add(new ChordRepetitionRater(1));
        subRaters.add(new NoSilenceRater(1));
        subRaters.add(new CrazyNoteOctaveRater(1));
        Crossover crossover = new Crossover(4);
        crossover.setMinDuration(50);
        crossover.setMaxDuration(200);
=======
        allMut.add(new RandomNoteMutator(c.MUTATOR_RANDOM_NOTE_PROBABILITY, c.MUTATOR_RANDOM_NOTE_STEP_RANGE));
        allMut.add(new OctaveMutator(c.MUTATOR_OCTAVE_PROBABILITY, c.MUTATOR_OCTAVE_RANGE));
        allMut.add(new ReverseMutator(c.MUTATOR_REVERSE_PROBABILITY, c.MUTATOR_REVERSE_NBR_OF_NEIGHBORS, c.MUTATOR_REVERSE_RANGE, true));
        allMut.add(new ScaleOfFifthMutator(c.MUTATOR_SCALE_OF_FIFTH_PROBABILITY, c.MUTATOR_SCALE_OF_FIFTH_RANGE));
        allMut.add(new SimplifyMutator(c.MUTATOR_SIMPLIFY_PROBABILITY, c.MUTATOR_SIMPLIFY_NBR_OF_NEIGHBORS, c.MUTATOR_SIMPLIFY_PROBABILITY));
        List<SubRater> subRaters = new LinkedList<SubRater>();
        subRaters.add(new MelodyRepetionRater(c.RATER_MELODY_REPETITION_WEIGHT));
        subRaters.add(new ScaleWhizz(c.RATER_SCALE_WEIGHT));
        subRaters.add(new BeatRepetitionRater(c.RATER_BEAT_REPETITION_WEIGHT));
        subRaters.add(new ChordRepetitionRater(c.RATER_CHORD_REPETITION_WEIGHT));
        Crossover crossover = new Crossover(c.CROSSOVER_NBR_OF_INTERSECTS);
        crossover.setMinDuration(c.CROSSOVER_MIN_DURATION);
        crossover.setMaxDuration(c.CROSSOVER_MAX_DURATION);
>>>>>>> 320d955cecba46c76d1204180d138c733f972546
        
        GeneticAlgorithm ga = new GeneticAlgorithm(selectedSongs, new Mutator(allMut, c.MUTATION_INITIAL_PROBABILITY, c.MUTATION_MINIMUM_PROBABILITY, c.MUTATION_PROBABILITY_RATIO), crossover, new Rater(subRaters));
        ga.setMinimumIterations(iterations);
        System.out.println("Start iterating");
        ga.iterate();
        //Translator.INSTANCE.saveSongToMidi(ga.getBest(), "best");
        Translator.INSTANCE.playSong(ga.getBest());
        return true;
    }
    
    @Override
    public Set<String> getArgSet() {
        return Sets.newHashSet("numberOfIterations");
    }

}