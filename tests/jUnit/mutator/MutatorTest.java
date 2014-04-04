package jUnit.mutator;

import jUnit.Helpers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.RandomNoteMutator;

public class MutatorTest {

    Song testSong;
    List<ISubMutator> subMutators;

    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     */
    @Before
    public void setUpSong() {
        testSong = Helpers.createTestSong();
        subMutators = new ArrayList<ISubMutator>();
        subMutators.add(new RandomNoteMutator(1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decreasingRatioThresholdTest() {
        Mutator m = new Mutator(subMutators, 1, 0.5, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void minimumMutationValueTest() {
        Mutator m = new Mutator(subMutators, 0.5, 1, 0.6);
    }

    @Test
    public void minimumProbabilityThresholdTest() {
        Mutator m = new Mutator(subMutators, 1, 0.5, 0.01);
        for (int i = 0; i < 1000; i++) {
            m.updateMutationProbability();
        }
        assertTrue(
                "Old probability: " + m.getInitialMutationProbability()
                        + "\nNew Probability: "
                        + m.getCurrentMutationProbability()
                        + "\nMinimum probability: "
                        + m.getMinimumMutationProbability(),
                m.getCurrentMutationProbability() == m
                        .getMinimumMutationProbability());
    }

}
