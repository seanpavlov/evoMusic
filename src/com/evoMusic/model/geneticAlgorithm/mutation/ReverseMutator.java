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
    public ReverseMutator(double mutationProbability,
            int nbrOfAdditionalReversing, int nbrRange, boolean withRhythmLength) {
        super(mutationProbability);
        this.nbrOfAdditionalReversing = nbrOfAdditionalReversing;
        this.nbrRange = nbrRange;
        this.withRhythmLength = withRhythmLength;
    }

    /**
     * Mutate the note with noteIndex of song.
     */
    @Override
    public void mutate(Song song, int noteIndex) {
        if (Math.random() < this.getProbability()) {
            if (noteIndex > 0) {
                MidiUtil mu = new MidiUtil();
                int nbrOfTotalReversing = nbrOfAdditionalReversing + 1
                        + ((int) Math.random() * nbrRange);
                ArrayList<Integer> noteIndexes = new ArrayList<Integer>();
                ArrayList<Note> notes = new ArrayList<Note>();
                noteIndexes.add(noteIndex);
                notes.add(song.getTrack(0).getPart().getPhrase(0)
                        .getNote(noteIndex));
                int currentIndex = noteIndex - 1;
                noteIteration: for (int i = 1; i < nbrOfTotalReversing; i++) {
                    while (mu.isBlank(currentIndex) && currentIndex >= 0) {
                        currentIndex--;
                    }
                    if (currentIndex < 0) {
                        break noteIteration;
                    } else {
                        noteIndexes.add(currentIndex);
                        notes.add(song.getTrack(0).getPart().getPhrase(0)
                                .getNote(currentIndex));
                    }
                }
                int totalReverses = noteIndexes.size();
                for (int j = 0; j < noteIndexes.size(); j++) {
                    if (withRhythmLength) {
                        song.getScore()
                                .getPart(0)
                                .getPhrase(0)
                                .setNote(notes.get(totalReverses - 1 - j),
                                        noteIndexes.get(j));
                    } else {
                        int newPitch = notes.get(totalReverses - 1 - j)
                                .getPitch();
                        song.getTrack(0).getPart().getPhrase(0)
                                .getNote(noteIndexes.get(j)).setPitch(newPitch);
                    }
                }
            }
        }
    }

}
