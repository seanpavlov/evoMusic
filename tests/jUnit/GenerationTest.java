package jUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;

public class GenerationTest {
    
    private Rater rater;
    private Random rand;
    private Generation generation;
    private Crossover crossover;
    private Mutator mutator;
    private List<Individual> parents;

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
        List<SubRater> subRaterList = new ArrayList<SubRater>();
        subRaterList.add(new TestSubRater());
        rater = new Rater(subRaterList);
        rand = new Random();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGeneration() {
        List<Individual> parents = new ArrayList<Individual>();
        this.generation = new Generation(parents);
        assertNull(generation.getChildren());
        assertNotNull(generation.getParents());
    }

    @Test
    public void testGetParents() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetChildren() {
        fail("Not yet implemented");
    }

    @Test
    public void testMakeChildren() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetBestChildren() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetBestIndividuals() {
        fail("Not yet implemented");
    }

}
