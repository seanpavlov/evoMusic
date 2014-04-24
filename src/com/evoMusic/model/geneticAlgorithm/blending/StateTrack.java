package com.evoMusic.model.geneticAlgorithm.blending;

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
    private static final int RHYTHM_PRECISION = 1000000;

    private int firstNote;
    private int instrument;
    private int channel;
    private TrackTag tag;
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
        if (part.size() == 0) {
            firstNote = 60;
            tag = TrackTag.NONE;
            return;
        }
        tag = track.getTag();
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

        firstNote = formatNote(sortedStateList.get(0).notes.get(0).getPitch());
        int numberOfStates = sortedStateList.size();
        ComparableState currentState = null;
        ComparableState nextState = null;
        int lowestRyV = 0;
        for (int i = 0; i < numberOfStates - 1; i++) {
            currentState = sortedStateList.get(i);
            nextState = sortedStateList.get(i + 1);

            // if only rests, add 1 rest and lowest RyV.
            // else, add all notes that are not rests and lowest RyV from those
            // non-rests.
            lowestRyV = Integer.MAX_VALUE;
            if (currentState.containsOnlyRests()) {
                for (Note note : currentState.notes) {
                    if (note.getRhythmValue() < lowestRyV) {
                        lowestRyV = (int)(note.getRhythmValue() * RHYTHM_PRECISION);
                    }
                }
            }
            

            currentNote = sortedNoteList.get(i).note;
            nextNote = sortedNoteList.get(i + 1).note;
            // adding to intervals
            intervals[i] = formatNote(nextNote.getPitch())
                    - formatNote(currentNote.getPitch());

            // adding to rythmValues.
            rhythmValues[i] = currentNote.getRhythmValue();

            // adding to duration.
            durations[i] = currentNote.getDuration();

            // adding to dynamics.
            dynamics[i] = currentNote.getDynamic();
        }
        currentNote = sortedNoteList.get(numberOfStates - 1).note;
        rhythmValues[numberOfStates - 1] = currentNote.getRhythmValue();
        durations[numberOfStates - 1] = currentNote.getDuration();
        dynamics[numberOfStates - 1] = currentNote.getDynamic();
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

        this.firstNote = firstNote;
        this.instrument = instrument;
        this.channel = channel;
        this.intervals = intervals;
        this.rhythmValues = rhythmValues;
        this.durations = durations;
        this.dynamics = dynamics;
        this.tag = tag;
    }

    /**
     * Creates Track object converted from this interval track.
     * 
     * @return The track made from this interval track's properties.
     */
    public Track toTrack() {
        Phrase phrase = new Phrase(0.0);
        int currentPitch = getFirstNote();
        Note newNote = new Note(currentPitch, getRythmValues()[0]);
        newNote.setDuration(getDurations()[0]);
        newNote.setDynamic(getDynamics()[0]);
        phrase.add(newNote);
        for (int j = 0; j < intervals.length; j++) {
            currentPitch += intervals[j];
            if (currentPitch < 0) {// || currentPitch > 127) {
                newNote = new Note(Note.REST, rhythmValues[j + 1]);

            } else {
                newNote = new Note(currentPitch, rhythmValues[j + 1]);
            }
            newNote.setDuration(durations[j + 1]);
            newNote.setDynamic(dynamics[j + 1]);
            phrase.add(newNote);
        }
        Part newPart = new Part();
        newPart.add(phrase);
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
     * Gets the array of intervals for the notes of this interval track.
     * 
     * @return The array of intervals for the notes of this interval track.
     */
    public int[] getIntervals() {
        return intervals;
    }

    /**
     * Gets the array of rhythm values for the notes of this interval track.
     * 
     * @return The array of rhythm values for the notes of this interval track.
     */
    public double[] getRythmValues() {
        return rhythmValues;
    }

    /**
     * Gets the array of durations for the notes of this interval track.
     * 
     * @return The array of durations for the notes of this interval track.
     */
    public double[] getDurations() {
        return durations;
    }

    /**
     * Gets the array of dynamics for the notes of this interval track.
     * 
     * @return The array of dynamics for the notes of this interval track.
     */
    public int[] getDynamics() {
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
            ;
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
