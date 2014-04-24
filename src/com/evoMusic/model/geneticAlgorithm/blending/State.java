package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.List;
import jm.music.data.Note;

public class State {

    private static final int REST_PITCH = -1000000;
    public static final int RHYTHM_PRECISION = 1000000;
    
    private List<Integer> intervals;
    private Double rhythmValue;
    private List<Double> durations;
    private List<Integer> dynamics;
    
    public State(List<Note> notes, Integer prevPitch) {
        
        formatState(notes);
        
        for (Note note : notes) {
            intervals.add(formatNote(note.getPitch()) - formatNote(prevPitch));
            durations.add(note.getDuration());
            dynamics.add(note.getDynamic());
        }
        rhythmValue = notes.get(0).getRhythmValue();
        
    }
    
    /**
     * Changes the value of a pitch if it is a rest. This is to avoid the
     * interval int to flip.
     * 
     * @param inputNote
     * @return REST_PITCH if note is rest, inputNote otherwise.
     */
    private int formatNote(int inputNote) {
        if (inputNote == Note.REST) {
            return REST_PITCH;
        }
        return inputNote;
    }
    
    private void formatState(List<Note> notes) {
        int lowestRyV = 0;
        // if only rests, add 1 rest and lowest RyV.
        // else, add all notes that are not rests and lowest RyV from those
        // non-rests.
        lowestRyV = Integer.MAX_VALUE;
        // if not only rests, remove all rests.
        if (!currentState.containsOnlyRests()) {
            for (Note note : currentState.notes) {
                if (note.isRest()) {
                    currentState.notes.remove(note);
                }
            }
        }
        // find lowest rhythm value.
        for (Note note : currentState.notes) {
            if (note.getRhythmValue() < lowestRyV) {
                lowestRyV = (int)(note.getRhythmValue() * RHYTHM_PRECISION);
            }
        }
        if (currentState.containsOnlyRests()) {
            
        }
    }
}
