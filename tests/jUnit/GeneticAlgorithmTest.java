package jUnit;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.DrCross;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.Individual;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.RandomNotePitchMutator;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.CrazyNoteOctaveRater;
import com.evoMusic.model.geneticAlgorithm.rating.LcmPitchRater;
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
import com.evoMusic.model.geneticAlgorithm.rating.ScaleWhizz;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.model.geneticAlgorithm.rating.ZipfsLawRater;
import com.evoMusic.parameters.P;
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
        testSong1.addTagToTrack(1, TrackTag.NONE);
        testSong1.addTagToTrack(2, TrackTag.NONE);
        testSong2 = Translator.INSTANCE.loadMidiToSong("midifiles/test_piano.mid");
        testSong2.addTagToTrack(0, TrackTag.MELODY);
        testSong2.addTagToTrack(1, TrackTag.NONE);
        List<Song> inputSongs = new ArrayList<Song>();
        inputSongs.add(testSong1);
        inputSongs.add(testSong2);
        
        List<ISubMutator> allMut = new ArrayList<ISubMutator>();
        allMut.add(new RandomNotePitchMutator(0.5, 6));
        DrCross crossover = new DrCross(P.CROSSOVER_NBR_OF_INTERSECTS);
        
        List<SubRater> subRaters = new LinkedList<SubRater>();        
        subRaters.add(new MelodyRepetionRater(1));
        subRaters.add(new ScaleWhizz(1));
        subRaters.add(new BeatRepetitionRater(1));
        subRaters.add(new ChordRepetitionRater(1));
        subRaters.add(new MelodyRepetionRater(1));
        subRaters.add(new ScaleWhizz(1));
        subRaters.add(new BeatRepetitionRater(1));
        subRaters.add(new ChordRepetitionRater(1));
        subRaters.add(new CrazyNoteOctaveRater(1));
        subRaters.add(new MelodyDirectionStabilityRater(1));
        subRaters.add(new PitchVarietyRater(1));
        subRaters.add(new MelodyDirectionRater(1));
        subRaters.add(new MelodyNoteDensityVarietyRater(1));
        subRaters.add(new RhythmicVarietyRater(1));
        subRaters.add(new NoSilenceRater(1));
        subRaters.add(new MelodyPitchRangeRater(1));
        subRaters.add(new RepeatedPitchDensityRater(1));
        subRaters.add(new MelodyRestDensityVarietyRater(1));
        subRaters.add(new ZipfsLawRater(1));
        subRaters.add(new MelodyNoteSyncopationRater(1));
        subRaters.add(new LcmPitchRater(1));
        
        ga = new GeneticAlgorithm(inputSongs,
                new Mutator(allMut, 1), crossover, new Rater(
                        subRaters), 100, 2, 2, 1, 40);
    }

    @Test
    public void betterSongTest() {
        int lowGenerations = 1;
        int highGenerations = 100;
        Individual individual = ga.generateGenerations(lowGenerations);
        Individual newIndividual = ga.generateGenerations(highGenerations);
        assertTrue("Generation " + lowGenerations+ ":\t" + individual.getRating() + "\nGeneration " + highGenerations + ":\t" + newIndividual.getRating(), individual.getRating() < newIndividual.getRating());
    }
    
    @Test
    public void rightNumberOfGenerationsTest() {
        int nbrOfGenerations = 20;
        ga.generateGenerations(nbrOfGenerations);
        assertTrue("Inserted nbr of Generation:\t" + nbrOfGenerations + "\nNumber of iterations:\t" + ga.getCurrentIteration(), nbrOfGenerations == ga.getCurrentIteration());
    }
    
    @Test
    public void rightRatingThresholdTest() {
        double threshold = 0.3;
        ga.generateUntilRating(threshold);
        assertTrue("Rating threshold:\t" + threshold + "\nBest song rating:\t" + ga.getBestIndividual().getRating(), threshold <= ga.getBestIndividual().getRating());
    }

}
