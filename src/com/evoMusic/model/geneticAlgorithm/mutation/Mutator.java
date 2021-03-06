package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.parameters.P;
import com.evoMusic.util.Sort;

public class Mutator {
    private List<ISubMutator> subMutators;
    private double mutationProbability;
    private double probabilityMultiplier;

    /**
     * Handles all mutations. Iterate through every note in the song and then
     * mutates depending on the probability.
     * 
     * @param subMutators
     *            is a list of all sub mutators that handles mutation.
     * @param mutationProbability
     *            is the initial probability of mutation.
     * @param minimumMutationProbability
     *            is the lower bound of mutation probability, if it uses
     *            probability decrease ratio.
     * @param probabilityDecreaseRatio
     *            is the ratio which it lowers the probability of mutation in
     *            each generation. If it is set to 0, it will not be used.
     */
    public Mutator(List<ISubMutator> subMutators, double mutationProbability) {
        this.subMutators = subMutators;
        this.mutationProbability = mutationProbability;
        probabilityMultiplier = 1;
    }

    /**
     * Iterate through each note and mutate the song. It will iterate through
     * all sub mutators and use these depending on their local probabilities.
     * 
     * @param individual
     */
    public void mutate(Song individual) {
        if (Math.random() < mutationProbability) {
            for (ISubMutator subMutator : subMutators) {
                subMutator.mutate(individual, probabilityMultiplier);
                individual.flattern();
            }
        }
    }

    /**
     * Update the multiplier of the local probabilities, the sub mutator
     * probability, to be the current multiplier times the decrease ratio.
     * 
     */
    public void updateProbabilityMultiplier() {
        probabilityMultiplier -= P.MUTATION_LOCAL_PROBABILITY_MULTIPLIER_DECREASE_RATIO;
        if (probabilityMultiplier < P.MUTATION_LOCAL_PROBABILITY_MINIMUM_MULTIPLIER) {
            probabilityMultiplier = P.MUTATION_LOCAL_PROBABILITY_MINIMUM_MULTIPLIER;
        }
    }

    /**
     * Get the current value of the probability multiplier.
     * 
     * @return probability multiplier as a double.
     */
    public double getProbabilityMultiplier() {
        return probabilityMultiplier;
    }
}
