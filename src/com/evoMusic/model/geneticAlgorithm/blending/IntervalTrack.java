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
import jm.music.data.Score;

import com.evoMusic.model.Track;

public class IntervalTrack {

    private int firstNote;
    private int instrument;
    private int channel;
    private int[] intervals;
    private double[] rythmValues;
    private double[] durations;

    public IntervalTrack(Track track) {
        Map<Double, List<ComparableNote>> noteMap;
        List<ComparableNote> sortedNoteList = new ArrayList<ComparableNote>();
        Part part = track.getPart();
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
        instrument = part.getInstrument();
        channel = part.getChannel();
        int numberOfNotes = sortedNoteList.size();
        intervals = new int[numberOfNotes - 1];
        rythmValues = new double[numberOfNotes];
        durations = new double[numberOfNotes];
        Note currentNote = null;
        Note nextNote = null;
        for (int i = 0; i < numberOfNotes - 1; i++) {
            currentNote = sortedNoteList.get(i).note;
            nextNote = sortedNoteList.get(i + 1).note;
            // adding to intervals
            intervals[i] = nextNote.getPitch() - currentNote.getPitch();

            // adding to rythmValues.
            rythmValues[i] = currentNote.getRhythmValue();

            // adding to duration
            durations[i] = currentNote.getDuration();
        }
        rythmValues[numberOfNotes - 1] = sortedNoteList.get(numberOfNotes - 1).note
                .getRhythmValue();
    }

    public IntervalTrack(int firstNote, int instrument, int channel,
            int[] intervals, double[] rythmValues, double[] durations) {

        this.firstNote = firstNote;
        this.instrument = instrument;
        this.channel = channel;
        this.intervals = intervals;
        this.rythmValues = rythmValues;
        this.durations = durations;
    }

    public Track toTrack() {
        Phrase phrase = new Phrase(0.0);
        int currentPitch = getFirstNote();
        Note newNote = new Note(currentPitch, getRythmValues()[0]);
        newNote.setDuration(getDurations()[0]);
        phrase.add(newNote);
        for (int j = 0; j < intervals.length; j++) {
            currentPitch += intervals[j];
            if (currentPitch < 0 || currentPitch > 127) {
                newNote = new Note(Note.REST, rythmValues[j + 1]);

            } else {
                newNote = new Note(currentPitch, rythmValues[j + 1]);
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

    public int getFirstNote() {
        return firstNote;
    }

    public int getInstrument() {
        return instrument;
    }

    public int getChannel() {
        return channel;
    }

    public int[] getIntervals() {
        return intervals;
    }

    public double[] getRythmValues() {
        return rythmValues;
    }

    public double[] getDurations() {
        return durations;
    }

    private class ComparableNote implements Comparable<ComparableNote> {

        private Note note;
        private double startTime;

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
