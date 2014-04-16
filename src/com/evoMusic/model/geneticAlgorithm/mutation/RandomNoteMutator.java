package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import jm.music.data.Note;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.util.MidiUtil;
import com.evoMusic.util.Sort;

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
    public void mutate(Song individual) {
            MidiUtil mu = new MidiUtil();
            Random ra = new Random();

            int nbrOfTracks = individual.getScore().getPartArray().length;
            for (int track = 0; track < nbrOfTracks; track++) {
                List<List<Note>> noteList = Sort.getSortedNoteList(individual
                        .getScore().getPart(track));
                int nbrOfNotes = noteList.size();
                for (int currentNoteId = 0; currentNoteId < nbrOfNotes; currentNoteId++) {
                    if (Math.random() < getProbability()) {
                        int currentStep = 0;
                        double sumOfProbability = 0;
                        double r = Math.random();
                        sumOfProbability = probabilityList[currentStep];
                        while (sumOfProbability < r) {
                            currentStep++;
                            sumOfProbability += probabilityList[currentStep];
                        }
                        int nbrOfSteps = currentStep + 1;
                        List<Note> paralellList = noteList.get(currentNoteId);
                        int nbrOfParalellNotes = paralellList.size();
                        int selectedPhrase = ra.nextInt(nbrOfParalellNotes);
                        Note currentNote = paralellList.get(selectedPhrase);
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
