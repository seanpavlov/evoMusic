package jUnit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ScaleOfFifthMutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;
import com.evoMusic.util.Translator;

public class GeneticAlgorithmTest {

    private Rater rater;
    private List<Song> parents;
    private GeneticAlgorithm testGA;
    private Song testParent;
    private static int maxTimePerGeneration = 50; // in millisec

    private class TestRater extends SubRater {
        
        private double step = 0.1;
        private double x = 0;
        
        public TestRater() {
            this.setWeight(1);
        }

        @Override
        public double rate(Song song) {
            x += step;
            return 1-1/(x+1);
        }
    }

    @Before
    public void setUp() throws Exception {
        initGA();
    }

    private void initGA() {
        rater = new Rater();
        rater.addSubRater(new TestRater());
        testParent = Translator.INSTANCE
                .loadMidiToSong("midifiles/mm2wily1.mid");
        testParent.addTagToTrack(0, TrackTag.MELODY);
        parents = new ArrayList<Song>();
        parents.add(testParent);
        parents.add(testParent);
        List<ISubMutator> subMutators = new ArrayList<ISubMutator>();
        subMutators.add(new ScaleOfFifthMutator(0.5, 1));
        Mutator mutator = new Mutator(subMutators, 0.5);
        Crossover crossover = new Crossover(8);
        testGA = new GeneticAlgorithm(parents, mutator, crossover, rater);
        testGA.setThrowAwayFirstParents(false);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGeneticAlgorithm() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetIterationsDone() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFirstParents() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetElitism() {
        fail("Not yet implemented");
    }

    @Test
    public void testIterate() {
        fail("Not yet implemented");
    }

    @Test
    public void testIterateInt() {
        double firstRating;
        double prevBestRating;
        double newBestRating;

        firstRating = testGA.getGeneration().getBestIndividuals(1).get(0)
                .getRating();
        newBestRating = firstRating;
        for (int i = 0; i < 100; i++) {
            prevBestRating = testGA.getGeneration().getBestIndividuals(1)
                    .get(0).getRating();
            testGA.iterate(1);
            newBestRating = testGA.getGeneration().getBestIndividuals(1).get(0)
                    .getRating();

            assertTrue(prevBestRating <= newBestRating);
        }
        assertTrue("First rating: " + firstRating + "\nFinal rating: "
                + newBestRating, firstRating < newBestRating);
    }

    @Test
    public void testGetBestChild() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetBest() {
        fail("Not yet implemented");
    }

}
