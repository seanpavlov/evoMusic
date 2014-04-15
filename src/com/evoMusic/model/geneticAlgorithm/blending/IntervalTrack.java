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

public class IntervalTrack {

    private static final int REST_PITCH = -1000000;

    private int firstNote;
    private int instrument;
    private int channel;
    private int[] intervals;
    private double[] rhythmValues;
    private double[] durations;

    /**
     * Creates a new interval track from a given track, maintaining its
     * properties.
     * 
     * @param track
     *            The track from which this instance will be based on.
     */
    public IntervalTrack(Track track) {
        Part part = track.getPart();
        instrument = part.getInstrument();
        channel = part.getChannel();
        if (part.size() == 0) {
            firstNote = 60;
            intervals = new int[0];
            rhythmValues = new double[0];
            durations = new double[0];
            return;
        }
        Map<Double, List<ComparableNote>> noteMap;
        List<ComparableNote> sortedNoteList = new ArrayList<ComparableNote>();
        noteMap = new HashMap<Double, List<ComparableNote>>();
        double partLength = 0;
        for (Phrase phrase : part.getPhraseArray()) {
            partLength = phrase.getStartTime();
            for (Note note : phrase.getNoteArray()) {
                if (!noteMap.containsKey(partLength)) {
                    noteMap.put(partLength, new ArrayList<ComparableNote>());
                }
                noteMap.get(partLength).add(
                        new ComparableNote(note, partLength));
                partLength += note.getRhythmValue();
            }
        }
        // Currently only takes one note at the same time.
        sortedNoteList.clear();
        Iterator<List<ComparableNote>> iterator = noteMap.values().iterator();
        while (iterator.hasNext()) {
            sortedNoteList.add(iterator.next().get(0));
        }
        Collections.sort(sortedNoteList);

        firstNote = sortedNoteList.get(0).note.getPitch();
        int numberOfNotes = sortedNoteList.size();
        intervals = new int[numberOfNotes - 1];
        rhythmValues = new double[numberOfNotes];
        durations = new double[numberOfNotes];
        Note currentNote = null;
        Note nextNote = null;
        for (int i = 0; i < numberOfNotes - 1; i++) {
            currentNote = sortedNoteList.get(i).note;
            nextNote = sortedNoteList.get(i + 1).note;
            // adding to intervals
            intervals[i] = formatNote(nextNote.getPitch()) - formatNote(currentNote.getPitch());

            // adding to rythmValues.
            rhythmValues[i] = currentNote.getRhythmValue();

            // adding to duration
            durations[i] = currentNote.getDuration();
        }
        currentNote = sortedNoteList.get(numberOfNotes - 1).note;
        rhythmValues[numberOfNotes - 1] = currentNote.getRhythmValue();
        durations[numberOfNotes - 1] = currentNote.getDuration();
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
    public IntervalTrack(int firstNote, int instrument, int channel,
            int[] intervals, double[] rhythmValues, double[] durations) {

        this.firstNote = firstNote;
        this.instrument = instrument;
        this.channel = channel;
        this.intervals = intervals;
        this.rhythmValues = rhythmValues;
        this.durations = durations;
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
        phrase.add(newNote);
        for (int j = 0; j < intervals.length; j++) {
            currentPitch += intervals[j];
            if (currentPitch < 0) {// || currentPitch > 127) {
                newNote = new Note(Note.REST, rhythmValues[j + 1]);

            } else {
                newNote = new Note(currentPitch, rhythmValues[j + 1]);
            }
            newNote.setDuration(durations[j + 1]);
            phrase.add(newNote);
        }
        Part newPart = new Part();
        newPart.add(phrase);
        newPart.setChannel(getChannel());
        newPart.setInstrument(getInstrument());
        return new Track(newPart);
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
     * A private class containing a note and its start time. The note is
     * comparable by its start time.
     */
    private class ComparableNote implements Comparable<ComparableNote> {

        private Note note;
        private double startTime;

        /**
         * Creates a new comparable note with the given note and its start time.
         * 
         * @param note
         *            The given note.
         * @param startTime
         *            The start time of the given note.
         */
        public ComparableNote(Note note, double startTime) {
            this.note = note;
            this.startTime = startTime;
        }

        @Override
        public int compareTo(ComparableNote o) {
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
