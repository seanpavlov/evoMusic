package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.ArrayList;
import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.util.MidiUtil;

public class Mutator {
    private List<ISubMutator> subMutators;
    private double initialMutationProbability;
    private double currentMutationProbability;
    private double minimumMutationProbability;
    private double probabilityDecreaseRatio;
    private boolean probabilityDecreaseRatioActive;
    private MidiUtil mu = new MidiUtil();

    /**
     * Handles all mutations. Iterate through every note in the song and then
     * mutates depending on the probability.
     * 
     * @param subMutators
     *            is a list of all sub mutators that handles mutation.
     * @param initialMutationProbability
     *            is the initial probability of mutation.
     * @param minimumMutationProbability
     *            is the lower bound of mutation probability, if it uses
     *            probability decrease ratio.
     * @param probabilityDecreaseRatio
     *            is the ratio which it lowers the probability of mutation in
     *            each generation. If it is set to 0, it will not be used.
     */
    public Mutator(List<ISubMutator> subMutators,
            double initialMutationProbability,
            double minimumMutationProbability, double probabilityDecreaseRatio) {
        this.subMutators = subMutators;
        this.initialMutationProbability = initialMutationProbability;
        this.currentMutationProbability = initialMutationProbability;
        if (minimumMutationProbability > initialMutationProbability) {
            throw new IllegalArgumentException(
                    "Minimum probability must be lower than current mutation probability");
        } else {
            this.minimumMutationProbability = minimumMutationProbability;
        }
        if (probabilityDecreaseRatio <= 0) {
            if (probabilityDecreaseRatio < 0) {
                throw new IllegalArgumentException(
                        "Ratio lower than zero. Decreasing probability ratio disabled.");
            } else {
                probabilityDecreaseRatioActive = false;
            }
        } else {
            this.probabilityDecreaseRatio = probabilityDecreaseRatio;
            probabilityDecreaseRatioActive = true;
        }
    }

    /**
     * Iterate through each note and mutate the song. It will iterate through
     * all sub mutators and use these depending on their local probabilities.
     * 
     * @param individual
     */
    public void mutate(Song individual) {
        int nbrOfNotes = 0;
        try {
            nbrOfNotes = individual.getTrack(0).getPart().getPhrase(0)
                    .getNoteArray().length;
        } catch (Exception e) {
            System.out.println("Could not mutate " + individual.getTitle());
        }
        for (int i = 0; i < nbrOfNotes; i++) {
            if (!mu.isBlank(individual.getTrack(0).getPart().getPhrase(0)
                    .getNote(i).getPitch())) {
                if (Math.random() < currentMutationProbability) {
                    for (ISubMutator subMutator : subMutators) {
                        if (Math.random() < subMutator.getProbability()) {
                            subMutator.mutate(individual, i);
                        }
                    }
                }
            }
        }
    }

    /**
     * Called after each generation to update the mutation probability if it
     * uses decreasing mutation ratio.
     */
    public void updateMutationProbability() {
        if (probabilityDecreaseRatioActive) {
            double newProbability = currentMutationProbability
                    - probabilityDecreaseRatio;
            if (newProbability > minimumMutationProbability) {
                currentMutationProbability = newProbability;
            } else {
                currentMutationProbability = minimumMutationProbability;
            }
        }
    }

    public double getCurrentMutationProbability() {
        return currentMutationProbability;
    }

    public double getMinimumMutationProbability() {
        return minimumMutationProbability;
    }

    public double getInitialMutationProbability() {
        return initialMutationProbability;
    }
}
