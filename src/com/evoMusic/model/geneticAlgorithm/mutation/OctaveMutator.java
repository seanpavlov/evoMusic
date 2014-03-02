package com.evoMusic.model.geneticAlgorithm.mutation;

import jm.music.data.Note;

import com.evoMusic.model.Song;

public class OctaveMutator extends ISubMutator {
    private int octaveRange;

    /**
     * Raises or lower the note pitch in steps of octaves.
     * 
     * @param mutationProbability
     *            is the probability of mutation.
     * @param octaveRange
     *            is the range that it can change in octave.
     */
    public OctaveMutator(double mutationProbability, int octaveRange) {
        super(mutationProbability);
        this.octaveRange = octaveRange;
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
            int nbrOfSteps = (int) ((Math.random() * octaveRange) + 1);
            int pitchNbr = note.getPitch();
            if (mu.canRaiseNote(pitchNbr, mu.NBR_OF_NOTES * nbrOfSteps)) {
                if (mu.canLowerNote(pitchNbr, mu.NBR_OF_NOTES * nbrOfSteps)) {
                    if (Math.random() < 0.5) {
                        note.setPitch(pitchNbr - (mu.NBR_OF_NOTES * nbrOfSteps));
                    } else {
                        note.setPitch(pitchNbr + (mu.NBR_OF_NOTES * nbrOfSteps));
                    }
                } else {
                    note.setPitch(pitchNbr + (mu.NBR_OF_NOTES * nbrOfSteps));
                }
            } else if (mu.canLowerNote(pitchNbr, mu.NBR_OF_NOTES * nbrOfSteps)) {
                note.setPitch(pitchNbr - (mu.NBR_OF_NOTES * nbrOfSteps));
            }
            song.getScore().getPart(0).getPhrase(0).setNote(note, noteIndex);

        }
    }

}
