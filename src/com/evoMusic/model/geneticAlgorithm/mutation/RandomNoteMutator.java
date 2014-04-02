package com.evoMusic.model.geneticAlgorithm.mutation;

import jm.music.data.Note;

import com.evoMusic.model.Song;
import com.evoMusic.util.MidiUtil;

public class RandomNoteMutator extends ISubMutator {
    private int halfStepRange;
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
    public RandomNoteMutator(double mutationProbability, int halfStepRange) {
        super(mutationProbability);
        this.halfStepRange = halfStepRange;
        probabilityList = new double[halfStepRange];
        double sum = 0;
        double temp = 0;
        for (int i = 0; i < halfStepRange; i++) {
            if (i % 2 == 0) {
                temp = (halfStepRange - i) / (double) halfStepRange;
                probabilityList[i] = temp;
                sum += temp;
            } else {
                temp = (halfStepRange - (i - 1)) / (double) halfStepRange;
                probabilityList[i] = temp;
                sum += temp;
            }
        }

        for (int j = 0; j < halfStepRange; j++) {
            probabilityList[j] = probabilityList[j] / sum;
        }
    }

    @Override
    public void mutate(Song song, int noteIndex) {
        if (Math.random() < this.getProbability()) {
            MidiUtil mu = new MidiUtil();

            int currentStep = 0;
            double r = Math.random();
            double sumOfProbability = probabilityList[currentStep];
            while (sumOfProbability < r) {
                currentStep++;
                sumOfProbability += probabilityList[currentStep];
            }
            int nbrOfSteps = currentStep + 1;
            Note currentNote = song.getTrack(0).getPart().getPhrase(0)
                    .getNote(noteIndex);
            int pitchNbr = currentNote.getPitch();
            if (mu.canRaiseNote(pitchNbr, nbrOfSteps)) {
                if (mu.canLowerNote(pitchNbr, nbrOfSteps)) {
                    if (Math.random() < 0.5) {
                        currentNote.setPitch(pitchNbr - nbrOfSteps);
                    } else {
                        currentNote.setPitch(pitchNbr + nbrOfSteps);
                    }
                } else {
                    currentNote.setPitch(pitchNbr + nbrOfSteps);
                }
            } else if (mu.canLowerNote(pitchNbr, nbrOfSteps)) {
                currentNote.setPitch(pitchNbr - nbrOfSteps);
            }
        }

    }

}
