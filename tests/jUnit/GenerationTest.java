package jUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.Generation;
import com.evoMusic.model.geneticAlgorithm.Individual;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;
import com.evoMusic.util.Translator;

public class GenerationTest {

    private Rater rater;
    private Random rand;
    private Generation generation;
    private Crossover crossover;
    private Mutator mutator;
    private List<Individual> parents;
    private Song testSong;

    private class TestSubRater extends SubRater {

        private double weight = 1;

        @Override
        public double rate(Song song) {
            return rand.nextDouble();
        }

        @Override
        public double getWeight() {
            return 1;
        }

        @Override
        public void setWeight(double weight) {
            this.weight = weight;
        }

        @Override
        public boolean shouldRate() {
            return this.weight > 0.0;
        }

    }

    @Before
    public void setUp() throws Exception {
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        testSong.addTagToTrack(0, TrackTag.MELODY);
        List<SubRater> subRaterList = new ArrayList<SubRater>();
        subRaterList.add(new TestSubRater());
        rater = new Rater(subRaterList);
        crossover = new Crossover(5);
        mutator = new Mutator(new ArrayList<ISubMutator>(), 0.1);
        rand = new Random();
        parents = new ArrayList<Individual>();
        this.generation = new Generation(parents);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGeneration() {
        assertNull(generation.getChildren());
        assertNotNull(generation.getParents());
    }

    @Test
    public void testGetParents() {
        int numberOfParents = 5;
        for (int i = 0; i < numberOfParents; i++) {
            parents.add(new Individual(testSong, rater.rate(testSong)));
        }
        this.generation = new Generation(parents);
        assertEquals(numberOfParents, generation.getParents().size());
    }

    @Test
    public void testMakeChildren() {
        assertNull(generation.getChildren());
        int numberOfChildren = 10;
        parents.add(new Individual(testSong, rater.rate(testSong)));
        parents.add(new Individual(testSong, rater.rate(testSong)));
        this.generation = new Generation(parents);
        generation.makeChildren(crossover, mutator, rater, numberOfChildren);
        assertEquals(numberOfChildren, generation.getChildren().size());
    }

    @Test
    public void testGetBestChildren() {
        int numberOfChildren = 10;
        assertNull(generation.getBestChildren(5));
        parents.add(new Individual(testSong, rater.rate(testSong)));
        parents.add(new Individual(testSong, rater.rate(testSong)));
        this.generation = new Generation(parents);
        generation.makeChildren(crossover, mutator, rater, numberOfChildren);
        assertEquals(5, generation.getBestChildren(5).size());
        assertEquals(numberOfChildren,
                generation.getBestChildren(numberOfChildren).size());
        assertEquals(numberOfChildren,
                generation.getBestChildren(numberOfChildren + 2).size());
        for (Individual ind : generation.getChildren()) {
            assertTrue("Not best child", ind.getRating() <= generation
                    .getBestChildren(1).get(0).getRating());
        }
    }

    @Test
    public void testGetBestIndividuals() {
        int numberOfChildren = 10;
        assertNotNull(generation.getBestIndividuals(5));
        parents.add(new Individual(testSong, -1));
        parents.add(new Individual(testSong, -1));
        this.generation = new Generation(parents);
        assertNotNull(generation.getBestIndividuals(5));
        assertEquals(2, generation.getBestIndividuals(5).size());
        assertEquals(1, generation.getBestIndividuals(1).size());

        generation.makeChildren(crossover, mutator, rater, numberOfChildren);
        assertEquals(5, generation.getBestIndividuals(5).size());
        assertEquals(numberOfChildren,
                generation.getBestIndividuals(numberOfChildren).size());
        assertEquals(numberOfChildren + 2,
                generation.getBestIndividuals(numberOfChildren + 4).size());

        List<Individual> bestList = new ArrayList<Individual>(
                generation.getChildren());
        bestList.addAll(generation.getParents());
        for (Individual ind : bestList) {
            assertTrue("Comparing against best individual",
                    ind.getRating() <= generation.getBestIndividuals(1).get(0)
                            .getRating());
        }
    }

}
