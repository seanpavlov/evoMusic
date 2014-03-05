package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.ArrayList;
import java.util.List;

import com.evoMusic.model.Song;

public class Mutator {
    private List<ISubMutator> subMutators = new ArrayList<ISubMutator>();
    private double overallMutationProbability;
    private MidiUtil mu = new MidiUtil();
    
    /**
     * 
     * @param subMutators
     * @param overallMutationProbability
     */
    public Mutator(List<ISubMutator> subMutators, double overallMutationProbability) {
        this.subMutators = subMutators;
        this.overallMutationProbability = overallMutationProbability;
    }

    /**
     * 
     * @param individual
     */
    public void mutate(Song individual) {
        int nbrOfNotes = individual.getScore().getPart(0).getPhrase(0).getNoteArray().length;
        
        for (int i = 0; i < nbrOfNotes; i++) {
            if (!mu.isBlank(individual.getScore().getPart(0).getPhrase(0).getNote(i).getPitch())){
                if (Math.random() < overallMutationProbability) {
                    for(ISubMutator subMutator : subMutators){
                        if(Math.random() < subMutator.getProbability()){
                            subMutator.mutate(individual, i);
                        }
                    }
                }
            }
        }
    }    
}
