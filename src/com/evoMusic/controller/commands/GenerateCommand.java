package com.evoMusic.controller.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.OctaveMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.RandomNoteMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ReverseMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ScaleOfFifthMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.SimplifyMutator;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.CrazyNoteOctaveRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyDirectionStabilityRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRepetionRater;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.ScaleWhizz;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.Parameters;
import com.google.common.collect.Sets;

public class GenerateCommand extends AbstractCommand {

    private List<Song> selectedSongs;
    private Semaphore finished = new Semaphore(0);
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
        final int iterations = Integer.parseInt(args[0]);
        List<ISubMutator> allMut = new ArrayList<ISubMutator>();
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
        subRaters.add(new CrazyNoteOctaveRater(c.RATER_CRAZY_OCTAVE_WEIGHT));
        subRaters.add(new MelodyDirectionStabilityRater(c.RATER_MELODY_DIRECTION_WEIGHT));
        Crossover crossover = new Crossover(c.CROSSOVER_NBR_OF_INTERSECTS);
        crossover.setMinDuration(c.CROSSOVER_MIN_DURATION);
        crossover.setMaxDuration(c.CROSSOVER_MAX_DURATION);
        
        final GeneticAlgorithm ga = new GeneticAlgorithm(selectedSongs, new Mutator(allMut, c.MUTATION_INITIAL_PROBABILITY, c.MUTATION_MINIMUM_PROBABILITY, c.MUTATION_PROBABILITY_RATIO), crossover, new Rater(subRaters));
        
        ga.setMinimumIterations(iterations);
        System.out.println("Start iterating");
        
        runProgress(ga, iterations);
        ga.iterate();
        finished.acquireUninterruptibly();
        
        //Translator.INSTANCE.saveSongToMidi(ga.getBest(), "best");
        Translator.INSTANCE.playSong(ga.getBest());
        return true;
    }
    
    private void runProgress(final GeneticAlgorithm ga, final int iterations) {
        new Thread(new Runnable() {
            final int width = 50;
            final int height = 20;
            final boolean eclipseHack = true;
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int i = 0;
                String newLine = "\r";
                if (eclipseHack) {
                    StringBuilder newLines = new StringBuilder();
                    for (; i < height; i ++){
                        newLines.append("\n");
                    }
                    newLine = newLines.toString();
                } 
                
                System.out.print(newLine + "[");
                i = 0;
                for (; i < (int)((1.0*ga.getIterationsDone() / iterations )*width); i++) {
                  System.out.print("#");
                }
                for (; i < width; i++) {
                  System.out.print(" ");
                }
                System.out.print("] "+ga.getIterationsDone()*100 / iterations + "%");
                if (ga.getIterationsDone() / iterations == 1) {
                    System.out.println();
                    finished.release();
                    return;
                } else {
                    run();
                }
            }
        }).start();
    }
    
    @Override
    public Set<String> getArgSet() {
        return Sets.newHashSet("numberOfIterations");
    }

}
