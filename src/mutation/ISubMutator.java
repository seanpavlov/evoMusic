package mutation;

import jm.music.data.Phrase;

public interface ISubMutator {
    
    /**
     * Mutates a phrase and send it back.
     * @param origPhrase is the phrase to be mutated
     * @return the mutated phrase
     */
    public void mutate(Phrase origPhrase, double probability);

    /**
     * Get the probability of using the sub-mutator.
     * @return
     */
    public double getProbability();

}
