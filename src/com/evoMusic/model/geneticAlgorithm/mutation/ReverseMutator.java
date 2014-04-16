package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.ArrayList;

import jm.music.data.Note;

import com.evoMusic.model.Song;
import com.evoMusic.util.MidiUtil;

public class ReverseMutator extends ISubMutator {
    private int nbrOfAdditionalReversing;
    private int nbrRange;
    private boolean withRhythmLength;

    /**
     * Reverse a part of the track by selecting a number of notes and put them
     * in the song in reversed order again.
     * 
     * @param mutationProbability
     *            is the probability of the mutation.
     * @param nbrOfAdditionalReversing
     *            is the number of neighboring notes to current note that is
     *            going to be part of of the reversing.
     * @param nbrRange
     *            is the range of neighboring notes it can be. It will range
     *            between nbrOfAdditionalReversing and
     *            nbrOfAdditionalReversing+nbrRange.
     * @param withRhythmLength
     *            is if it should take the rhythm length into the reversing.
     *            True if it should and false otherwise.
     */
    public ReverseMutator(double mutationProbability, boolean withRhythmLength) {
        super(mutationProbability);
    }

    /**
     * Mutate the note with noteIndex of song.
     */
    @Override
    public void mutate(Song individual) {
        //TODO: Fix when getSegment is done in Track-class.
    }
}
