package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Note;

public class State implements Comparable<State> {

    // Used for comparing
    private static final double RHYTHM_VALUE_PRECISION = 0.000001;

    private double              startTime;
    private double              rhythmValue;
    private int                 highestPitch;
    private boolean             onlyRests;
    private List<Integer>       intervals;
    private List<Double>        durations;
    private List<Integer>       dynamics;
    private List<Note>          notes;

    public State(double startTime) {
        this.startTime = startTime;
        this.notes = new ArrayList<Note>();
        this.intervals = new ArrayList<Integer>();
        this.durations = new ArrayList<Double>();
        this.dynamics = new ArrayList<Integer>();
        this.onlyRests = true;
    }

    public void addNote(Note note) {
        notes.add(note);
        if (onlyRests && note.getPitch() != Note.REST) {
            onlyRests = false;
        }
    }

    private void initialize(int previousPitch) {
        this.rhythmValue = Double.MAX_VALUE;
        this.highestPitch = 0;
        for (Note note : notes) {
            // Find the smallest rhythm value.
            if (note.getRhythmValue() < this.rhythmValue) {
                /*
                 * Only select IF (only rests AND note is rest) OR (NOT(only
                 * rests) AND NOT(note is rest))
                 */
                if ((onlyRests && note.getPitch() == Note.REST)
                        || (!onlyRests && note.getPitch() != Note.REST)) {
                    this.rhythmValue = note.getRhythmValue();
                }
            }
            // Find the highest pitch.
            if (note.getPitch() > this.highestPitch) {
                this.highestPitch = note.getPitch();
            }
        }

        /*
         * Sort out so that there are either only non-rests if there are
         * non-rests in the list, or so that there are only one rest if there
         * are only rests in it
         */
        if (onlyRests) {
            for (Note note : notes) {
                if (Math.abs(note.getRhythmValue() - this.rhythmValue)) {

                }
            }

        } else {

        }
    }

    @Override
    public int compareTo(State arg0) {
        if (Math.abs(startTime - arg0.startTime) < RHYTHM_VALUE_PRECISION) {
            return 0;
        }
        if (startTime < arg0.startTime) {
            return -1;
        } else {
            return 1;
        }
    }
}
