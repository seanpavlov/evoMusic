package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import com.evoMusic.model.Song;

public class MarkovChain {

    /**
     * The number of decimals kept in timeDiff.
     */
    private static final int timeDiffAccuracy = 10;
    private static final int numberOfIntervalLookbacks = 3;
    
    private Song originalSong;
    private Random rand;

    List<int[]> originalIntervals;
    List<double[]> originalDurations;

    public MarkovChain(Song song) {
        rand = new Random();
        originalSong = song;
        // List<ComparableNote> allNotes;
        originalIntervals = new ArrayList<int[]>();
        originalDurations = new ArrayList<double[]>();
        Map<Double, List<ComparableNote>> noteMap;
        List<ComparableNote> sortedNoteList = new ArrayList<ComparableNote>();

        for (Part part : song.getScore().getPartArray()) {
            noteMap = new HashMap<Double, List<ComparableNote>>();
            double songLength;
            for (Phrase phrase : part.getPhraseArray()) {
                songLength = phrase.getStartTime();
                for (Note note : phrase.getNoteArray()) {
                    if (!noteMap.containsKey(songLength)) {
                        noteMap.put(songLength, new ArrayList<ComparableNote>());
                    }
                    noteMap.get(songLength).add(new ComparableNote(note, songLength));
                    songLength += note.getDuration();
                }
            }
            // Currently only takes one note at the same time.
            sortedNoteList.clear();
            Iterator<List<ComparableNote>> iterator = noteMap.values().iterator();
            while (iterator.hasNext()) {
                sortedNoteList.add(iterator.next().get(0));
            }
            Collections.sort(sortedNoteList);
            int numberOfNotes = sortedNoteList.size();
            int[] intervals = new int[numberOfNotes-1];
            originalIntervals.add(intervals);
            double[] durations = new double[numberOfNotes];
            originalDurations.add(durations);
            Note currentNote = null;
            Note nextNote = null;
            for (int i = 0; i < numberOfNotes - 1; i++) {
                currentNote = sortedNoteList.get(i).note;
                nextNote = sortedNoteList.get(i + 1).note;
                // adding to intervals
                intervals[i] = nextNote.getPitch() - currentNote.getPitch();

                // adding to duration
                durations[i] = currentNote.getDuration();
            }
            if (nextNote != null) {
                durations[numberOfNotes - 1] = nextNote.getDuration();
            }
        }
    }
    
    private Song toSong(List<int[]> intervals, List<double[]> durations, int[] firstNotes) {
        // All arguments must be of the same size/length!
        Score score = new Score();
        Phrase phrase;
        int currentPitch;
        int[] currentPartsIntervals;
        double[] currentPartsDurations;
        for(int i = 0; i < intervals.size(); i++) {
            phrase = new Phrase();
            currentPitch = firstNotes[i];
            phrase.add(new Note(currentPitch, durations.get(i)[0]));
            currentPartsIntervals = intervals.get(i);
            currentPartsDurations = durations.get(i);
            for(int j = 0; j < currentPartsIntervals.length; j++) {
                currentPitch += currentPartsIntervals[j];
                phrase.add(new Note(currentPitch, currentPartsDurations[j+1]));
            }
            score.add(new Part(phrase));
        }
        
        return new Song(score);
    }
    
    private int getRandomNote(Phrase[] phrases) {
        return 0;
    }

    public Song generateNew() {
        int[] firstNotes = new int[1];
        firstNotes[0] = originalSong.getScore().getPart(0).getPhrase(0).getNote(0).getPitch();
        Song newSong = toSong(originalIntervals, originalDurations, firstNotes);
        newSong.getScore().setTempo(originalSong.getTempo());
        return newSong;
        
//        double longestDuration = 0;
//        double currentDuration;
//        for(double[] durations : originalDuration) {
//            currentDuration = 0;
//            for(double duration : durations) {
//                currentDuration += duration;
//            }
//            if(currentDuration > longestDuration) {
//                longestDuration = currentDuration;
//            }
//        }
//        return generateNew(longestDuration);
    }

    public Song generateNew(double maxSongDuration) {
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
    
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < originalIntervals.size(); i++) {
            string.append("Track 1: number of notes = " + originalDurations.get(i).length + "\n");
            string.append("Intervals: ");
            for(Integer interval : originalIntervals.get(i)) {
                string.append(interval);
                string.append(" ");
            }
            string.append("\nDurations: ");
            for(Double duration : originalDurations.get(i)) {
                string.append(duration);
                string.append(" ");
            }
        }
        
        return string.toString();
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
