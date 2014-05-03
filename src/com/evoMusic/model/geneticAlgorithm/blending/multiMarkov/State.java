package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jm.music.data.Note;

/**
 * 
 * Note that this class has a natural ordering inconsistent with equals.
 * 
 */
public class State implements Comparable<State> {

    // Used for comparing.
    private static final double RHYTHM_VALUE_PRECISION = 0.000001;
    // To avoid integer overflow.
    private static final int    REST_VALUE             = -1000000;
    // Might pe pointless, just to be safe...
    private static final int    REST_DYNAMIC           = 60;

    private double              startTime;
    private double              rhythmValue;
    private int                 highestPitch;
    private boolean             onlyRests;
    private int                 size;
    private List<Integer>       intervals;
    private List<Double>        durations;
    private List<Integer>       dynamics;
    private List<Note>          notes;

    public State(double startTime) {
        this.startTime = startTime;
        this.notes = new ArrayList<Note>();
        this.onlyRests = true;
        this.size = 0;
        highestPitch = REST_VALUE;
    }

    public void addNote(Note note) {
        notes.add(note);
        if (onlyRests && note.getPitch() != Note.REST) {
            onlyRests = false;
        }
        if (note.getPitch() > highestPitch) {
            highestPitch = note.getPitch();
        }
    }

    public boolean startsAtTime(double startTime) {
        return Math.abs(this.startTime - startTime) < RHYTHM_VALUE_PRECISION;
    }

    public void initializeIntervalForm(int previousPitch) {
        this.intervals = new ArrayList<Integer>();
        this.durations = new ArrayList<Double>();
        this.dynamics = new ArrayList<Integer>();
        this.rhythmValue = Double.MAX_VALUE;
        for (Note note : notes) {
            // Find the smallest rhythm value.
            if (note.getRhythmValue() < this.rhythmValue) {
                /*
                 * Only select IF (only rests AND note is rest) OR (NOT(only
                 * rests) AND NOT(note is rest))
                 */
                if ((onlyRests && note.isRest())
                        || (!onlyRests && !note.isRest())) {
                    this.rhythmValue = note.getRhythmValue();
                }
            }
        }

        /*
         * Sort out so that there are either only non-rests if there are
         * non-rests in the list, or so that there are only one rest if there
         * are only rests in it.
         */
        if (onlyRests) {
            intervals.add(REST_VALUE - previousPitch);
            durations.add(rhythmValue);
            dynamics.add(REST_DYNAMIC);
        } else {
            // Make sure to add them in order, with the largest interval first.
            List<Note> tempNoteList = new ArrayList<>(this.notes);
            Collections.sort(tempNoteList, IntervalComparator.INSTANCE);
            for (Note note : tempNoteList) {
                if (!note.isRest()) {
                    intervals.add(note.getPitch() - previousPitch);
                    durations.add(note.getDuration());
                    dynamics.add(note.getDynamic());
                }
            }
        }
        this.size = intervals.size();
    }

    /**
     * Gets the number of notes in this state.
     * 
     * @return The number of notes in this state.
     */
    public int size() {
        return this.size;
    }

    /**
     * Gets the highest pitch in this state. If the lowest pitch is a rest, it
     * will return this.REST_PITCH instead.
     * 
     * @return The highest pitch in this state.
     */
    public int getHighestPitch() {
        return this.highestPitch;
    }
    
    public int getHighestInterval() {
        return this.intervals.get(0);
    }
    
    public double getRhythmValue() {
        return this.rhythmValue;
    }

    public List<Note> toNotes(int previousPitch) {
        List<Note> tempNoteList = new ArrayList<Note>();
        Note tempNote;
        int newPitch;
        for (int i = 0; i < this.size(); i++) {
            newPitch = previousPitch + intervals.get(i);
            // If new note is rest or has climbed out of range, add a rest.
            if (newPitch < 0 || newPitch > 127) {
                tempNote = new Note(Note.REST, rhythmValue);
            } else {
                tempNote = new Note(newPitch, rhythmValue);
            }
            tempNote.setDuration(durations.get(i));
            tempNote.setDynamic(dynamics.get(i));
            tempNoteList.add(tempNote);
        }
        return tempNoteList;
    }

    /**
     * Compares with the start time of the other state.
     */
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

    /**
     * Is equals only if all intervals are equals in order and of the same size.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof State)) {
            return false;
        }
        State other = (State) o;
        if (this == other) {
            return true;
        }
        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (this.intervals.get(i) != other.intervals.get(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two notes on their pitch value.
     */
    public static class IntervalComparator implements Comparator<Note> {

        private static IntervalComparator INSTANCE = new IntervalComparator();

        @Override
        public int compare(Note arg0, Note arg1) {
            return arg0.getPitch() - arg1.getPitch();
        }

    }
}
