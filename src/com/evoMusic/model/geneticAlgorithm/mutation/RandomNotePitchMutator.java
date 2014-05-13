package com.evoMusic.model.geneticAlgorithm.mutation;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.util.Helpers;

public class RandomNotePitchMutator extends ISubMutator {
    private double[] probabilityList;

    /**
     * Mutates a note randomly in a given range. The closer to the original
     * note, the higher probability.
     * 
     * @param mutationProbability
     *            is the probability of this to mutate.
     * @param halfStepRange
     *            is the range that it can mutate in.
     */
    public RandomNotePitchMutator(double mutationProbability, int halfStepRange) {
        super(mutationProbability);
        probabilityList = new double[halfStepRange];
        double sum = 0;
        double temp = 0;
        for (int i = 0; i < halfStepRange; i++) {
            temp = (halfStepRange - (i - (i % 2)));
            probabilityList[i] = temp;
            sum += temp;
        }

        for (int j = 0; j < halfStepRange; j++) {
            probabilityList[j] = probabilityList[j] / sum;
        }
    }

    @Override
    public void mutate(Song individual, double probabilityMultiplier) {
        double localProbability = getProbability() * probabilityMultiplier;
        int notePitch;

        for (Part part : individual.getScore().getPartArray()) {
            for (Phrase phrase : part.getPhraseArray()) {
                for (Note note : phrase.getNoteArray()) {
                    notePitch = note.getPitch();
                    // TODO change second restriction to note != rest.
                    if (Math.random() < localProbability && notePitch < 128 && notePitch >= 0) {
                        note.setPitch(note.getPitch()
                                + chooseNewPitch(note.getPitch()));
                    }
                }
            }
        }
    }

    private int chooseNewPitch(int currentPitch) {

        double maxProb = 0.0;
        int listSize = probabilityList.length;
        int maxNotesUp;
        int maxNotesDown;

        if (currentPitch + listSize > 127) {
            maxNotesUp = 127 - currentPitch;
            for (int i = 0; i < maxNotesUp; i++) {
                maxProb += probabilityList[i];
            }
        } else {
            maxNotesUp = listSize;
            maxProb += 1.0;
        }
        if (currentPitch - listSize < 0) {
            maxNotesDown = currentPitch;
            for (int i = 0; i < maxNotesDown; i++) {
                maxProb += probabilityList[i];
            }
        } else {
            maxNotesDown = listSize;
            maxProb += 1.0;
        }

        double randomValue = Math.random() * maxProb;
        double currentRandom = 0;

        for (int i = 0; i < maxNotesUp; i++) {
            currentRandom += probabilityList[i];
            if (randomValue < currentRandom) {
                return i + 1;
            }
        }
        for (int i = 0; i < maxNotesDown; i++) {
            currentRandom += probabilityList[i];
            if (randomValue < currentRandom) {
                return -(i + 1);
            }
        }
        Helpers.LOGGER.warning("something went wrong in pitch mutator");
        return 0;
    }
}
