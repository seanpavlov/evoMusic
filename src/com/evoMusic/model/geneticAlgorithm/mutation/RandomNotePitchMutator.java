package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import jm.music.data.Note;

import com.evoMusic.model.Song;
import com.evoMusic.util.MidiUtil;
import com.evoMusic.util.Sort;

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
    public void mutate(Song individual) {
        MidiUtil mu = new MidiUtil();

        int nbrOfTracks = individual.getScore().getSize();
        for (int track = 0; track < nbrOfTracks; track++) {
            int nbrOfPhrases = individual.getScore().getPart(track).getSize();
            for(int phrase = 0; phrase < nbrOfPhrases; phrase++){
                int nbrOfNotes = individual.getScore().getPart(track).getPhrase(phrase).getSize();
                for(int note = 0; note < nbrOfNotes; note++){
                    if(Math.random() < getProbability()){
                        Note currentNote = individual.getScore().getPart(track).getPhrase(phrase).getNote(note);
                        int nbrOfSteps = randomizeNbrOfSteps();
                        int pitchNbr = currentNote.getPitch();
                        if (!mu.isBlank(pitchNbr)) {
                            if (mu.canRaiseNote(pitchNbr, nbrOfSteps)) {
                                if (mu.canLowerNote(pitchNbr, nbrOfSteps)) {
                                    if (Math.random() < 0.5) {
                                        currentNote.setPitch(pitchNbr
                                                - nbrOfSteps);
                                    } else {
                                        currentNote.setPitch(pitchNbr
                                                + nbrOfSteps);
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
            }
        }
    }
    
    private int randomizeNbrOfSteps(){
        double random = Math.random();
        double currentProbability = probabilityList[1];
        int currentStepId = 0;
        while(currentProbability < random){
            currentStepId++;
            currentProbability += probabilityList[currentStepId];
        }
        return currentStepId;
    }

}
