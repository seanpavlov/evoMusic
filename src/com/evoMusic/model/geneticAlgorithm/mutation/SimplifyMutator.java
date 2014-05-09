package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import jm.music.data.Note;
import com.evoMusic.model.*;
import com.evoMusic.util.MidiUtil;
import com.evoMusic.util.Sort;

public class SimplifyMutator extends ISubMutator {

    /**
     * Mutate so that past neighboring notes will have the same pitch as the
     * current node.
     * 
     * @param mutationProbability
     *            is the probability of this mutator to run.
     * @param nbrOfPastNeighbours
     *            is the number of past neighbors who can be mutated.
     * @param neighbourProbability
     *            is the probability of the specific neighbor to the mutated.
     */
    public SimplifyMutator(double mutationProbability) {
        super(mutationProbability);
    }

    /**
     * Mutate the note with noteIndex of song.
     */
    @Override
    public void mutate(Song individual, double probabilityMultiplier) {
        MidiUtil mu = new MidiUtil();
        double localProbability = getProbability()*probabilityMultiplier;

        int nbrOfTracks = individual.getScore().getPartArray().length;
        for (int track = 0; track < nbrOfTracks; track++) {
            int nbrOfPhrases = individual.getScore().getPart(track).getPhraseArray().length;
            for(int phrase = 0; phrase < nbrOfPhrases; phrase++){
                int nbrOfNotes = individual.getScore().getPart(track).getPhrase(phrase).getNoteArray().length;
                for(int note = 1; note < nbrOfNotes; note++){
                    if(Math.random() < localProbability){
                        Note currentNote = individual.getScore().getPart(track).getPhrase(phrase).getNote(note);
                        if(!mu.isBlank(currentNote.getPitch())){
                            individual.getScore().getPart(track).getPhrase(phrase).getNote(note-1).setPitch(currentNote.getPitch());
                        }
                    }
                }
            }
        }
    }

}
