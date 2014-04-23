package jUnit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.DrCross;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.Individual;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.OctaveMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.RandomNoteMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ReverseBarNotesMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.RhythmValueMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ScaleOfFifthMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.SimplifyMutator;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.CrazyNoteOctaveRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyDirectionRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyDirectionStabilityRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyNoteDensityRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyPitchRangeRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRepetionRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRestDensityRater;
import com.evoMusic.model.geneticAlgorithm.rating.NoSilenceRater;
import com.evoMusic.model.geneticAlgorithm.rating.PitchVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.RepeatedPitchDensityRater;
import com.evoMusic.model.geneticAlgorithm.rating.RhythmicVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.ScaleWhizz;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.model.geneticAlgorithm.rating.ZipfsLawRater;
import com.evoMusic.util.Parameters;
import com.evoMusic.util.TrackTag;

public class GeneticAlgorithmTest {

    Song testSong1;
    Song testSong2;
    GeneticAlgorithm ga;

    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     */
    @Before
    public void setUpSong() {
        testSong1 = Translator.INSTANCE.loadMidiToSong("midifiles/test_forest.mid");
        testSong1.addTagToTrack(0, TrackTag.MELODY);
        testSong1.addTagToTrack(1, TrackTag.MELODY);
        testSong1.addTagToTrack(2, TrackTag.RHYTHM);
        testSong2 = Translator.INSTANCE.loadMidiToSong("midifiles/test_piano.mid");
        testSong2.addTagToTrack(0, TrackTag.MELODY);
        testSong2.addTagToTrack(1, TrackTag.RHYTHM);
        List<Song> inputSongs = new ArrayList<Song>();
        inputSongs.add(testSong1);
        inputSongs.add(testSong2);
        
        Parameters c = Parameters.getInstance();
        List<ISubMutator> allMut = new ArrayList<ISubMutator>();
        allMut.add(new RandomNoteMutator(c.MUTATOR_RANDOM_NOTE_PROBABILITY,
                c.MUTATOR_RANDOM_NOTE_STEP_RANGE));
        allMut.add(new RhythmValueMutator(c.MUTATOR_RHYTHM_VALUE_PROBABILITY, c.MUTATOR_RHYTHM_VALUE_MOVING_RANGE));
        allMut.add(new ReverseBarNotesMutator(c.MUTATOR_REVERSE_PROBABILITY));
        allMut.add(new SimplifyMutator(c.MUTATOR_SIMPLIFY_PROBABILITY));
        allMut.add(new OctaveMutator(c.MUTATOR_OCTAVE_PROBABILITY,
                c.MUTATOR_OCTAVE_RANGE));
        allMut.add(new ScaleOfFifthMutator(
                c.MUTATOR_SCALE_OF_FIFTH_PROBABILITY,
                c.MUTATOR_SCALE_OF_FIFTH_RANGE));
        
         List<SubRater> subRaters = new LinkedList<SubRater>();
        subRaters
                .add(new MelodyRepetionRater(c.RATER_MELODY_REPETITION_WEIGHT));
        subRaters.add(new ScaleWhizz(c.RATER_SCALE_WEIGHT));
        subRaters.add(new BeatRepetitionRater(c.RATER_BEAT_REPETITION_WEIGHT));
        subRaters
                .add(new ChordRepetitionRater(c.RATER_CHORD_REPETITION_WEIGHT));
        DrCross crossover = new DrCross(1);
        subRaters.add(new CrazyNoteOctaveRater(c.RATER_CRAZY_OCTAVE_WEIGHT));
        subRaters.add(new MelodyDirectionStabilityRater(
                c.RATER_MELODY_DIRECTION_WEIGHT));
        subRaters.add(new PitchVarietyRater(c.RATER_PITCH_VARIETY_WEIGHT));
        subRaters.add(new MelodyDirectionRater(c.RATER_PITCH_DIRECTION_WEIGHT));
        subRaters.add(new MelodyNoteDensityRater(
                c.RATER_MELODY_NOTE_DENSITY_WEIGHT));
        subRaters
                .add(new RhythmicVarietyRater(c.RATER_RHYTHMIC_VARIETY_WEIGHT));
        subRaters.add(new NoSilenceRater(c.RATER_NO_SILENCE_WEIGHT));
        subRaters.add(new MelodyPitchRangeRater(
                c.RATER_MELODY_PITCH_RANGE_WEIGHT));
        subRaters.add(new RepeatedPitchDensityRater(
                c.RATER_REPEATED_PITCH_DENSITY_WEIGTH));
        subRaters.add(new MelodyRestDensityRater(
                c.RATER_MELODY_REST_DENSITY_WEIGHT));
        subRaters.add(new ZipfsLawRater(c.RATER_ZIPFS_LAW_WEIGHT));
        ga = new GeneticAlgorithm(inputSongs,
                new Mutator(allMut, c.MUTATION_INITIAL_PROBABILITY,
                        c.MUTATION_MINIMUM_PROBABILITY,
                        c.MUTATION_PROBABILITY_RATIO), crossover, new Rater(
                        subRaters), c.GA_POPULATION_SIZE, c.GA_NBR_OF_ELITISM_SONGS, c.GA_NBR_OF_CROSSOVER_SONGS );
    }

    @Test
    public void betterSongTest() {
        Individual individual = ga.generateGenerations(2);
        Individual newIndividual = ga.generateGenerations(100);
        assertTrue("Generation 1:\t" + individual.getRating() + "\nGeneration 1000:\t" + newIndividual.getRating(), individual.getRating() < newIndividual.getRating());
    }
    
    @Test
    public void rightNumberOfGenerations() {
        int nbrOfGenerations = 20;
        ga.generateGenerations(nbrOfGenerations);
        assertTrue("Inserted nbr of Generation:\t" + nbrOfGenerations + "\nNumber of iterations:t" + ga.getCurrentIteration(), nbrOfGenerations == ga.getCurrentIteration());
    }

}
