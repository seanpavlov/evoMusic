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
import com.evoMusic.model.geneticAlgorithm.mutation.RandomNotePitchMutator;
import com.evoMusic.util.Parameters;

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
        subMutators.add(new RandomNotePitchMutator(1, 1));
    }

    @Test
    public void minimumProbabilityMultiplierTest() {
        Mutator m = new Mutator(subMutators, 1);
        Parameters.getInstance().MUTATION_LOCAL_PROBABILITY_MINIMUM_MULTIPLIER = 0.1;
        Parameters.getInstance().MUTATION_LOCAL_PROBABILITY_MULTIPLIER_DECREASE_RATIO = 0.1;
        for (int i = 0; i < 10; i++) {
            m.updateProbabilityMultiplier();
        }
        assertTrue(
                "Old probability: " + 1
                        + "\nNew Probability: "
                        + m.getProbabilityMultiplier()
                        + "\nMinimum probability: "
                        + Parameters.getInstance().MUTATION_LOCAL_PROBABILITY_MINIMUM_MULTIPLIER,
                m.getProbabilityMultiplier() == Parameters.getInstance().MUTATION_LOCAL_PROBABILITY_MINIMUM_MULTIPLIER);
    }

}
