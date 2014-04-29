package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

public class StateTrack {

    private static final int REST_PITCH = -1000000;
    public static final int RHYTHM_PRECISION = 1000000;

    private int firstNote;
    private int instrument;
    private int channel;
    private TrackTag tag;
    private int largestChord;
    private List<List<Integer>> intervals;
    private List<Double> rhythmValues;
    private List<List<Double>> durations;
    private List<List<Integer>> dynamics;

    /**
     * Creates a new interval track from a given track, maintaining its
     * properties.
     * 
     * @param track
     *            The track from which this instance will be based on.
     */
    public StateTrack(Track track) {
        Part part = track.getPart();
        instrument = part.getInstrument();
        channel = part.getChannel();
        intervals = new ArrayList<List<Integer>>();
        rhythmValues = new ArrayList<Double>();
        durations = new ArrayList<List<Double>>();
        dynamics = new ArrayList<List<Integer>>();
        largestChord = 0;
        if (part.size() == 0) {
            firstNote = 60;
            tag = TrackTag.NONE;
            return;
        }
        tag = track.getTag();

        // sort by start time and group notes at the same start time.
        Map<Integer, ComparableState> noteMap;
        List<ComparableState> sortedStateList = new ArrayList<ComparableState>();
        noteMap = new HashMap<Integer, ComparableState>();
        int partLength = 0;
        for (Phrase phrase : part.getPhraseArray()) {
            partLength = (int) (phrase.getStartTime() * RHYTHM_PRECISION);
            for (Note note : phrase.getNoteArray()) {
                if (!noteMap.containsKey(partLength)) {
                    noteMap.put(partLength, new ComparableState(partLength));
                }
                noteMap.get(partLength).addNote(note);
                partLength += (int) (note.getRhythmValue() * RHYTHM_PRECISION);
            }
        }
        Iterator<ComparableState> iterator = noteMap.values().iterator();
        while (iterator.hasNext()) {
            sortedStateList.add(iterator.next());
        }
        Collections.sort(sortedStateList);

        // TODO fetch better first note, currently may be buggy.
        // Should be fixed, but still ugly...
        firstNote = formatNote(formatState(sortedStateList.get(0)).get(0).getPitch());
        int numberOfStates = sortedStateList.size();
        List<Note> currentState = null;
        Integer previousHeadInterval = firstNote;
        List<Integer> stateIntervals;
        List<Double> stateDurations;
        List<Integer> stateDynamics;
        int currentStateSize;
        int currentStartTime;
        int nextStartTime;
        // go through each note group.
        for (int i = 0; i < numberOfStates; i++) {
            currentState = formatState(sortedStateList.get(i));
            currentStateSize = currentState.size();
            currentStartTime = sortedStateList.get(i).startTime;
            if (i == numberOfStates + 1) {
                // if it is the last state
                double biggestRyV = 0;
                for (Note note : sortedStateList.get(i).notes) {
                    if (note.getRhythmValue() > biggestRyV) {
                        biggestRyV = note.getRhythmValue();
                    }
                }
                nextStartTime = currentStartTime
                        + (int) (biggestRyV * RHYTHM_PRECISION);
            } else {
                nextStartTime = sortedStateList.get(i + 1).startTime;
            }
            if (currentStateSize > largestChord) {
                largestChord = currentStateSize;
            }

            // maps interval to its original note index for mapping intervals to
            // their rhythm values etc..
            Map<Integer, Integer> intervalMap = new HashMap<Integer, Integer>();
            for (int j = 0; j < currentStateSize; j++) {
                intervalMap.put(j, currentState.get(j).getPitch()
                        - previousHeadInterval);
            }
            Iterator<Integer> iter = intervalMap.keySet().iterator();
            stateIntervals = new ArrayList<Integer>();
            while (iter.hasNext()) {
                stateIntervals.add(iter.next());
            }
            Collections.sort(stateIntervals);
            // by now duplicate intervals are gone and list are sorted with
            // highest note first.

            stateDurations = new ArrayList<Double>();
            stateDynamics = new ArrayList<Integer>();
            Note rightNote;
            Double newRhythmValue = ((double) (nextStartTime - currentStartTime))
                    / RHYTHM_PRECISION;
            for (Integer interval : stateIntervals) {
                rightNote = currentState.get(intervalMap.get(interval));
                // stateRhythmValues.add(newRhythmValue);//
                // rightNote.getRhythmValue());
                stateDurations.add(rightNote.getDuration());
                stateDynamics.add(rightNote.getDynamic());
            }
            previousHeadInterval += stateIntervals.get(0);

            intervals.add(stateIntervals);
            rhythmValues.add(newRhythmValue);
            durations.add(stateDurations);
            dynamics.add(stateDynamics);
        }
    }

    private List<Note> formatState(ComparableState state) {
        // if only rests, remove all but one rest.
        // else, remove all rests.

        // if not only rests, remove all rests.
        if (!state.containsOnlyRests()) {
            for (Note note : state.notes) {
                if (note.isRest()) {
                    state.notes.remove(note);
                }
            }
            // else, remove all but one rest.
        } else {
            for (int i = 0; i < state.notes.size() - 1; i++) {
                state.notes.remove(state.notes.get(i));
            }
        }
        return state.notes;
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

    /**
     * Creates a new interval track from the given properties.
     * 
     * @param firstNote
     *            The note that will come first when converting this interval
     *            track into a real track.
     * @param instrument
     *            The instrument of this track.
     * @param channel
     *            The channel of this track.
     * @param intervals
     *            A list of intervals that will define the notes of this track.
     * @param rhythmValues
     *            A list of rhythm values which corresponds to the intervals.
     * @param durations
     *            A list of durations which corresponds to the intervals.
     */
    public StateTrack(int firstNote, int instrument, int channel,
            int[] intervals, double[] rhythmValues, double[] durations,
            int[] dynamics, TrackTag tag) {

        // this.firstNote = firstNote;
        // this.instrument = instrument;
        // this.channel = channel;
        // this.intervals = intervals;
        // this.rhythmValues = rhythmValues;
        // this.durations = durations;
        // this.dynamics = dynamics;
        // this.tag = tag;
    }

    /**
     * Creates Track object converted from this interval track.
     * 
     * @return The track made from this interval track's properties.
     */
    public Track toTrack() {
        List<Phrase> phrases = new ArrayList<Phrase>();
        int currentHeadPitch = getFirstNote();
        for (int i = 0; i < largestChord; i++) {
            phrases.add(new Phrase(0.0));
        }

        Note newNote;// = new Note(currentPitch, getRythmValues()[0]);
        // newNote.setDuration(getDurations()[0]);
        // newNote.setDynamic(getDynamics()[0]);
        // phrase.add(newNote);
        List<Integer> currentIntervals;
        Double currentRhythmValue;
        List<Double> currentDurations;
        List<Integer> currentDynamics;
        int chordSize;
        int currentPitchInChord;

        // looping through each chord.
        for (int stateIndex = 0; stateIndex < intervals.size(); stateIndex++) {
            currentIntervals = intervals.get(stateIndex);
            currentRhythmValue = rhythmValues.get(stateIndex);
            currentDurations = durations.get(stateIndex);
            currentDynamics = dynamics.get(stateIndex);
            chordSize = currentIntervals.size();

            for (int noteIndex = 0; noteIndex < chordSize; noteIndex++) {
                currentPitchInChord = currentHeadPitch
                        + currentIntervals.get(noteIndex);
                if (currentPitchInChord < 0 || currentPitchInChord > 127) {
                    newNote = new Note(Note.REST, currentRhythmValue);
                } else {
                    newNote = new Note(currentPitchInChord, currentRhythmValue);
                }
                newNote.setDuration(currentDurations.get(noteIndex));
                newNote.setDynamic(currentDynamics.get(noteIndex));
                phrases.get(noteIndex).add(newNote);
            }
            // Make sure all phrases are the same length
            for (int i = chordSize; i < largestChord; i++) {
                newNote = new Note(Note.REST, currentRhythmValue);
                phrases.get(i).add(newNote);
            }
        }
        Part newPart = new Part();
        for (Phrase phrase : phrases) {
            newPart.add(phrase);
        }
        newPart.setChannel(getChannel());
        newPart.setInstrument(getInstrument());
        return new Track(newPart, tag);
    }

    /**
     * Gets the first note of this interval track.
     * 
     * @return The first note of this interval track.
     */
    public int getFirstNote() {
        return firstNote;
    }

    /**
     * Gets the instrument of this interval track.
     * 
     * @return The instrument of this interval track.
     */
    public int getInstrument() {
        return instrument;
    }

    /**
     * Gets the channel of this interval track.
     * 
     * @return The channel of this interval track.
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Gets the list of intervals for the notes of this interval track.
     * 
     * @return The list of intervals for the notes of this interval track.
     */
    public List<List<Integer>> getIntervals() {
        return intervals;
    }

    /**
     * Gets the list of rhythm values for the notes of this interval track.
     * 
     * @return The list of rhythm values for the notes of this interval track.
     */
    public List<Double> getRythmValues() {
        return rhythmValues;
    }

    /**
     * Gets the list of durations for the notes of this interval track.
     * 
     * @return The list of durations for the notes of this interval track.
     */
    public List<List<Double>> getDurations() {
        return durations;
    }

    /**
     * Gets the list of dynamics for the notes of this interval track.
     * 
     * @return The list of dynamics for the notes of this interval track.
     */
    public List<List<Integer>> getDynamics() {
        return dynamics;
    }

    /**
     * Gets the tag of this interval track.
     * 
     * @return The tag of this interval track.
     */
    public TrackTag getTag() {
        return tag;
    }

    /**
     * A private class containing a note and its start time. The note is
     * comparable by its start time.
     */
    private class ComparableState implements Comparable<ComparableState> {

        private List<Note> notes;
        /** Start time multiplied by RHYTHM_PRECISION */
        private int startTime;

        private boolean containsOnlyRests = true;

        /**
         * Creates a new comparable note with the given note and its start time.
         * 
         * @param note
         *            The given note.
         * @param startTime
         *            The start time of the given note.
         */
        public ComparableState(int startTime) {
            this.notes = new ArrayList<Note>();
            this.startTime = startTime;
        }

        public void addNote(Note note) {
            notes.add(note);
            if (containsOnlyRests && (!note.isRest())) {
                containsOnlyRests = false;
            }
        }

        public boolean containsOnlyRests() {
            return containsOnlyRests;
        }

        @Override
        public int compareTo(ComparableState o) {
            double otherStartTime = o.startTime;
            if (startTime < otherStartTime) {
                return -1;
            } else if (startTime > otherStartTime) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
