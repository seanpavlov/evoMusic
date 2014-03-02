package mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jm.music.data.Note;
import jm.music.data.Phrase;
import model.Song;

public class Mutator {
    private List<ISubMutator> subMutators = new ArrayList<ISubMutator>();
    private double overallMutationProbability;
    
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
            if (Math.random() < overallMutationProbability) {
                Note currentNote = individual.getScore().getPart(0).getPhrase(0).getNote(i);
                for(ISubMutator subMutator : subMutators){
                    if(Math.random() < subMutator.getProbability()){
                        subMutator.mutate(individual, i);
                    }
                }
            }
        }
    }    
}
