package com.evoMusic.model.geneticAlgorithm.mutation;

import jm.music.data.Note;
import com.evoMusic.model.*;
import com.evoMusic.util.MidiUtil;

public class ScaleOfFifthMutator extends ISubMutator {
    private int stepRange;

    /**
     * Mutate the note in the scale of fifth, which is five half steps in either
     * direction. Takes in stepRange which will change the pitch within the
     * given range.
     * 
     * @param mutationProbability
     *            is the probability of using this mutator
     * @param stepRange
     *            is the range that it can mutate the pitch within the rules of
     *            scale of fifths.
     */
    public ScaleOfFifthMutator(double mutationProbability, int stepRange) {
        super(mutationProbability);
        this.stepRange = stepRange;
    }

    /**
     * Mutate the note with noteIndex of song.
     */
    @Override
    public void mutate(Song song, int noteIndex) {
        if (Math.random() < this.getProbability()) {
            MidiUtil mu = new MidiUtil();
            Note note = song.getScore().getPart(0).getPhrase(0)
                    .getNote(noteIndex);
            int nbrOfSteps = (int) ((Math.random() * stepRange) + 1);
            int pitchNbr = note.getPitch();
            if (mu.canRaiseNote(pitchNbr, (5 * nbrOfSteps))) {
                if (mu.canLowerNote(pitchNbr, (5 * nbrOfSteps))) {
                    if (Math.random() < 0.5) {
                        note.setPitch(pitchNbr - (5 * nbrOfSteps));
                    } else {
                        note.setPitch(pitchNbr + (5 * nbrOfSteps));
                    }
                } else {
                    note.setPitch(pitchNbr + (5 * nbrOfSteps));
                }
            } else if (mu.canLowerNote(pitchNbr, (5 * nbrOfSteps))) {
                note.setPitch(pitchNbr - (5 * nbrOfSteps));
            }
            song.getScore().getPart(0).getPhrase(0).setNote(note, noteIndex);

        }
    }

}
