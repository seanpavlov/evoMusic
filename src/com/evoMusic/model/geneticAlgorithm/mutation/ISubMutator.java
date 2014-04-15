package com.evoMusic.model.geneticAlgorithm.mutation;

import com.evoMusic.model.Song;

public abstract class ISubMutator {
    
    private double mutationProbability;
    
    public ISubMutator(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }
    
    /**
     * Mutates a phrase and send it back.
     * @param origPhrase is the phrase to be mutated
     * @return the mutated phrase
     */
    abstract public void mutate(Song song);

    /**
     * Get the probability of using the sub-mutator.
     * @return
     */
    public double getProbability(){
        return mutationProbability;
    }
}
