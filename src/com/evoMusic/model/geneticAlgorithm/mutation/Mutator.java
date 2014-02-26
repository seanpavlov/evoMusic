package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.Vector;

import com.evoMusic.model.Song;

import jm.music.data.Note;
import jm.music.data.Phrase;

public class Mutator {
    private double mutationProbability;
    private static double JUST_AND_EQUAL_TEMPERAMENT_SCALE = Math.pow(2,
            (1.0 / 12.0));
    private static int STEP_RANGE = 3;
    private static double LOWER_NOTE_PROBABILITY = 0.5;
    private static double MINIMUM_FREQUENCY = 27.500;
    private static double MAXIMUM_FREQUENCY = 4186.0;
    private Phrase newTrack; 

    /**
     * Handling the mutation. Each instance will use their own parameters, then
     * it can be used for many songs if the user want to.
     * 
     * @param mutationProbability
     *            is the probability of the note to mutate.
     */
    public Mutator(double mutationProbability, Song individual) {
        this.mutationProbability = mutationProbability;
        newTrack = individual.getScore().getPart(0).getPhrase(0);
    }

    /**
     * Mutates the song with the probability mutationProbability. The new note
     * is within the interval 50-100 right now.
     * 
     * @param individual
     *            is the song to be mutated.
     * @return the mutated song.
     */
    public Song mutate(Song individual) {
        int nbrOfNotes = newTrack.getNoteArray().length;
        for (int i = 0; i < nbrOfNotes; i++) {
            if (Math.random() < mutationProbability) {
                double currentFrequency = newTrack.getNoteArray()[i]
                        .getFrequency();
                if (currentFrequency > MINIMUM_FREQUENCY
                        && currentFrequency < MAXIMUM_FREQUENCY) {
                    
//                    double newFrequency = changeEven(currentFrequency);
//                    double newFrequency = changeInFifthNote(currentFrequency);
                    
                    // Testing swap notes
                    Note temp = newTrack.getNoteArray()[i];
                    if(i-1 >= 0 && newTrack.getNoteArray()[i-1].getFrequency() > MINIMUM_FREQUENCY){
                        Note prevNote = newTrack.getNoteArray()[i-1];
                        newTrack.setNote(new Note(prevNote.getFrequency(), temp.getRhythmValue()), i);
                        newTrack.setNote(new Note(temp.getFrequency(), prevNote.getRhythmValue()), i-1);
                    }
//                    Note newNote = new Note(newFrequency,
//                            currentTrack.getNoteArray()[i].getRhythmValue());
//                    currentTrack.setNote(newNote, i);
                }
            }
        }
        Vector newPhraseList = new Vector();
        newPhraseList.add(newTrack);
        individual.getScore().getPart(0).setPhraseList(newPhraseList);
        return individual;
    }

    /**
     * Send in a note in form of frequency and choose how many half tones it
     * should be raised or lowered.
     * 
     * @param frequency
     *            the frequency of the initial note.
     * @param steps
     *            number of steps it should be changed.
     * @return the new note in frequency form.
     */
    private double changeNote(double frequency, int steps) {
        double newFrequency = frequency
                * Math.pow(JUST_AND_EQUAL_TEMPERAMENT_SCALE, steps);
        return newFrequency;
    }
    
    /**
     * Change a tone with 3, 5, 7... half-tones 
     * @param frequency is the frequency that should be changed.
     * @return the new frequency
     */
    private double changeEven(double frequency) {
        int steps = generateSteps();
        frequency = changeNote(frequency, (3+(2*(steps-1))));
        return frequency;
    }

    /**
     * Change the note in fifths.
     * @param frequency
     * @return
     */
    private double changeInFifthNote(double frequency) {
        int steps = generateSteps();
        System.out.print("Orig: " + frequency);
        double newFrequency = frequency;
        for (int i = 0; i < Math.abs(steps); i++) {
            if((((3.0 / 2.0) * newFrequency)) < (newFrequency*2)){
                newFrequency = ((3.0 / 2.0) * newFrequency);
            }else{
                newFrequency = ((3.0 / 4.0) * newFrequency);
            }
        }
        if (steps < 0) {
            newFrequency = frequency - (newFrequency - frequency);
        }
        System.out.println("\t New: " + newFrequency);
        if (newFrequency < MINIMUM_FREQUENCY
                || newFrequency > MAXIMUM_FREQUENCY) {
            return frequency;
        } else {
            return newFrequency;
        }
    }
    
    private void swapNotes(int x1, int x2){
        
    }
    
    private int generateSteps(){
        int nbrOfSteps = ((int) (Math.random() * STEP_RANGE)) + 1;
        if (Math.random() < LOWER_NOTE_PROBABILITY) {
            nbrOfSteps = -nbrOfSteps;
        }
        return nbrOfSteps;
    }
    
}
