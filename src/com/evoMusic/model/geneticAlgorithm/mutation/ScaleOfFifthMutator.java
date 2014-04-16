package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import jm.music.data.Note;
import com.evoMusic.model.*;
import com.evoMusic.util.MidiUtil;
import com.evoMusic.util.Sort;

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
    public void mutate(Song individual) {
        MidiUtil mu = new MidiUtil();
        Random ra = new Random();
        int nbrOfSteps = (int) (Math.random() * stepRange) + 1;
        int nbrOfTracks = individual.getNbrOfTracks();
        for (int track = 0; track < nbrOfTracks; track++) {
            List<List<Note>> noteList = Sort.getSortedNoteList(individual
                    .getScore().getPart(track));
            int nbrOfNotes = noteList.size();
            for (int currentNoteId = 0; currentNoteId < nbrOfNotes; currentNoteId++) {
                if (Math.random() < getProbability()) {
                    List<Note> paralellList = noteList.get(currentNoteId);
                    int nbrOfParalellNotes = paralellList.size();
                    int selectedPhrase = ra.nextInt(nbrOfParalellNotes);
                    Note currentNote = paralellList.get(selectedPhrase);
                    int pitchNbr = currentNote.getPitch();
                    if (!mu.isBlank(pitchNbr)) {
                        if (mu.canRaiseNote(pitchNbr, (5 * nbrOfSteps))) {
                            if (mu.canLowerNote(pitchNbr, (5 * nbrOfSteps))) {
                                if (Math.random() < 0.5) {
                                    currentNote.setPitch(pitchNbr
                                            - (5 * nbrOfSteps));
                                } else {
                                    currentNote.setPitch(pitchNbr
                                            + (5 * nbrOfSteps));
                                }
                            } else {
                                currentNote.setPitch(pitchNbr
                                        + (5 * nbrOfSteps));
                            }
                        } else if (mu.canLowerNote(pitchNbr, (5 * nbrOfSteps))) {
                            currentNote.setPitch(pitchNbr - (5 * nbrOfSteps));
                        }
                    }
                }
            }
        }

    }

}
