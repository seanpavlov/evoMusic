package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;

public class MarkovChain {

    /**
     * The number of decimals kept in timeDiff.
     */
    private static final int timeDiffAccuracy = 10;
    private static final int numberOfIntervalLookbacks = 3;

    Song song;
    List<int[]> originalIntervals;
    List<double[]> originalTimeDiff;
    List<double[]> originalDuration;

    public MarkovChain(Song song) {
        this.song = song;
        // List<ComparableNote> allNotes;
        originalIntervals = new ArrayList<int[]>();
        originalTimeDiff = new ArrayList<double[]>();
        originalDuration = new ArrayList<double[]>();
        Map<Double, List<Note>> noteMap;
        List<ComparableNote> sortedNoteList = new ArrayList<ComparableNote>();

        for (Part part : song.getScore().getPartArray()) {
            // allNotes = new ArrayList<ComparableNote>();
            noteMap = new HashMap<Double, List<Note>>();
            for (Phrase phrase : part.getPhraseArray()) {
                for (Note note : phrase.getNoteArray()) {
                    // allNotes.add(new ComparableNote(note));
                    double roundedTime = getRoundedStartTime(note);
                    if (!noteMap.containsKey(roundedTime)) {
                        noteMap.put(getRoundedStartTime(note),
                                new ArrayList<Note>());
                    }
                    noteMap.get(roundedTime).add(note);
                }
            }
            // Currently only takes one note at the same time.
            sortedNoteList.clear();
            Iterator<List<Note>> iterator = noteMap.values().iterator();
            while (iterator.hasNext()) {
                sortedNoteList.add(new ComparableNote(iterator.next().get(0)));
            }
            Collections.sort(sortedNoteList);
            int numberOfNotes = sortedNoteList.size();
            int[] intervals = new int[numberOfNotes];
            originalIntervals.add(intervals);
            double[] timeDiffs = new double[numberOfNotes];
            originalTimeDiff.add(timeDiffs);
            double[] durations = new double[numberOfNotes];
            originalDuration.add(durations);
            Note currentNote = null;
            Note nextNote = null;
            for (int i = 0; i < numberOfNotes - 1; i++) {
                currentNote = sortedNoteList.get(i).note;
                nextNote = sortedNoteList.get(i + 1).note;
                // adding to intervals
                intervals[i] = nextNote.getPitch() - currentNote.getPitch();

                // adding to timeDiff
                timeDiffs[i] = nextNote.getSampleStartTime()
                        - currentNote.getSampleStartTime();

                // adding to duration
                durations[i] = currentNote.getDuration();
            }
            if (nextNote != null) {
                durations[numberOfNotes - 1] = nextNote.getDuration();
            }
        }
    }

    public Song createSong() {
        return createSong(0);
    }

    public Song createSong(double maxSongDuration) {
        // TODO optimize this!
        List<List<Integer>> newIntervals = new ArrayList<List<Integer>>();
        List<List<Double>> newTimeDiff = new ArrayList<List<Double>>();
        List<List<Double>> newDuration = new ArrayList<List<Double>>();

        List<Integer> intervalList;
        List<Double> timeDiffList;
        List<Double> durationList;
        for (int i = 0; i < originalIntervals.size(); i++) {
            intervalList = new ArrayList<Integer>();
            timeDiffList = new ArrayList<Double>();
            durationList = new ArrayList<Double>();
            double songLength = 0;
            while (songLength < maxSongDuration) {
                // TODO lookup intervals, timediff and duration at same time.
            }

            newIntervals.add(intervalList);
            newTimeDiff.add(timeDiffList);
            newDuration.add(durationList);
        }

        // TODO convert to real notes.
        return null;
    }

    private static double getRoundedStartTime(Note note) {
        double startTime = note.getSampleStartTime();
        startTime = (int) (startTime * timeDiffAccuracy);
        startTime = startTime / timeDiffAccuracy;
        return startTime;
    }

    private class ComparableNote implements Comparable<ComparableNote> {

        private Note note;

        public ComparableNote(Note note) {
            this.note = note;
        }

        @Override
        public int compareTo(ComparableNote o) {
            double thisStartDuration = this.note.getSampleStartTime();
            double otherStartDuration = o.note.getSampleStartTime();
            if (thisStartDuration < otherStartDuration) {
                return -1;
            } else if (thisStartDuration > otherStartDuration) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
