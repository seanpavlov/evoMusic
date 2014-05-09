package com.evoMusic.controller.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.DrCross;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.Individual;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.RandomNotePitchMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ReverseBarNotesMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.RhythmValueMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.SwapSegmentMutator;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.CrazyNoteOctaveRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyDirectionRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyDirectionStabilityRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyNoteDensityVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyNoteSyncopationRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyPitchRangeRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRepetionRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRestDensityVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.NoSilenceRater;
import com.evoMusic.model.geneticAlgorithm.rating.PitchVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.RepeatedPitchDensityRater;
import com.evoMusic.model.geneticAlgorithm.rating.RhythmicVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.SegmentScaleRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.model.geneticAlgorithm.rating.ZipfsLawRater;
import com.evoMusic.util.Parameters;
import com.google.common.collect.Sets;

public class GenerateCommand extends AbstractCommand {

    private List<Song> selectedSongs;
    private Semaphore finished = new Semaphore(0);
    private Parameters c = Parameters.getInstance();

    /**
     * Creates the generate command
     * 
     * @param selectedSongs
     *            reference to the songs selected to be used for generating
     *            individuals
     */
    public GenerateCommand(List<Song> selectedSongs) {
        this.selectedSongs = selectedSongs;
    }

    @Override
    public boolean execute(String[] args) {
        if (selectedSongs.size() < 2) {
            return false;
        }
        final int iterations = Integer.parseInt(args[0]);
        List<ISubMutator> allMut = new ArrayList<ISubMutator>();
//        allMut.add(new OctaveMutator(c.MUTATOR_OCTAVE_PROBABILITY, c.MUTATOR_OCTAVE_RANGE));
//        allMut.add(new ReverseMutator(c.MUTATOR_REVERSE_PROBABILITY, c.MUTATOR_REVERSE_NBR_OF_NEIGHBORS, c.MUTATOR_REVERSE_RANGE, true));
//        allMut.add(new SimplifyMutator(c.MUTATOR_SIMPLIFY_PROBABILITY, c.MUTATOR_SIMPLIFY_NBR_OF_NEIGHBORS, c.MUTATOR_SIMPLIFY_PROBABILITY));
        allMut.add(new RandomNotePitchMutator(c.MUTATOR_RANDOM_NOTE_PITCH_PROBABILITY,c.MUTATOR_RANDOM_NOTE_PITCH_STEP_RANGE));
        allMut.add(new RhythmValueMutator(c.MUTATOR_RHYTHM_VALUE_PROBABILITY, c.MUTATOR_RHYTHM_VALUE_MOVING_RANGE));
        allMut.add(new ReverseBarNotesMutator(c.MUTATOR_REVERSE_PROBABILITY));
        allMut.add(new SwapSegmentMutator(c.MUTATOR_SWAP_SEGMENT_PROBABILITY));
        
        DrCross crossover = new DrCross(c.CROSSOVER_NBR_OF_INTERSECTS);

        List<SubRater> subRaters = new LinkedList<SubRater>();        
        subRaters.add(new SegmentScaleRater(c.RATER_SCALE_WEIGHT));
//        subRaters.add(new ScaleWhizz(c.RATER_SCALE_WEIGHT));
        subRaters.add(new BeatRepetitionRater(c.RATER_BEAT_REPETITION_WEIGHT));
        subRaters.add(new MelodyRepetionRater(c.RATER_MELODY_REPETITION_WEIGHT));
        subRaters.add(new ChordRepetitionRater(c.RATER_CHORD_REPETITION_WEIGHT));
        subRaters.add(new CrazyNoteOctaveRater(c.RATER_CRAZY_OCTAVE_WEIGHT));
        subRaters.add(new MelodyDirectionStabilityRater(c.RATER_MELODY_DIRECTION_WEIGHT));
        subRaters.add(new PitchVarietyRater(c.RATER_PITCH_VARIETY_WEIGHT));
        subRaters.add(new MelodyDirectionRater(c.RATER_PITCH_DIRECTION_WEIGHT));
        subRaters.add(new MelodyNoteDensityVarietyRater(c.RATER_MELODY_NOTE_DENSITY_WEIGHT));
        subRaters.add(new RhythmicVarietyRater(c.RATER_RHYTHMIC_VARIETY_WEIGHT));
        subRaters.add(new NoSilenceRater(c.RATER_NO_SILENCE_WEIGHT));
        subRaters.add(new MelodyPitchRangeRater(c.RATER_MELODY_PITCH_RANGE_WEIGHT));
        subRaters.add(new RepeatedPitchDensityRater(c.RATER_REPEATED_PITCH_DENSITY_WEIGTH));
        subRaters.add(new MelodyRestDensityVarietyRater(c.RATER_MELODY_REST_DENSITY_WEIGHT));
        subRaters.add(new ZipfsLawRater(c.RATER_ZIPFS_LAW_WEIGHT));
        subRaters.add(new MelodyNoteSyncopationRater(c.RATER_MELODY_NOTE_SUSTAIN_WEIGHT));
//        subRaters.add(new RandomRater(1));
        
        final GeneticAlgorithm ga = new GeneticAlgorithm(
                        selectedSongs,
                        new Mutator(allMut, c.MUTATION_PROBABILITY), 
                        crossover, 
                        new Rater(subRaters), 
                        c.GA_POPULATION_SIZE,
                        c.GA_NBR_OF_ELITISM_SONGS, 
                        c.GA_NBR_OF_CROSSOVER_SONGS,
                        c.MARKOV_LOOKBACKS, 
                        c.MARKOV_SONGDURATION);
        System.out.println("Start iterating");

        runProgress(ga, iterations);
        Individual bestIndividual = ga.generateGenerations(iterations);

        if(!Parameters.getInstance().IN_DEBUG_MODE){
            finished.acquireUninterruptibly();
        }

        Translator.INSTANCE.saveSongToMidi(bestIndividual.getSong(), "outputSong : " + bestIndividual.getRating());
        Translator.INSTANCE.play(bestIndividual.getSong());
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
                    for (; i < height; i++) {
                        newLines.append("\n");
                    }
                    newLine = newLines.toString();
                }

                System.out.print(newLine + "[");
                i = 0;
                for (; i < (int) ((1.0 * ga.getCurrentIteration() / iterations) * width); i++) {
                    System.out.print("#");
                }
                for (; i < width; i++) {
                    System.out.print(" ");
                }
                System.out.print("] " + ga.getCurrentIteration() * 100
                        / iterations + "%" + " | " + "Best rating: "
                        + ga.getBestIndividual().getRating());
                if (ga.getCurrentIteration() / iterations == 1) {
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
