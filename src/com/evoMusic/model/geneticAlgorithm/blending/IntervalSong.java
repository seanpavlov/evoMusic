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

import com.evoMusic.model.Song;

public class IntervalSong {

    private List<int[]> originalIntervals;
    private List<double[]> originalRythmValues;
    private List<double[]> originalDurations;
    private Note[] firstNotes;
    private int[] instruments;
    private int[] channels;
    private double tempo;

    public IntervalSong(List<int[]> intervals, List<double[]> rythmValues,
            List<double[]> durations, Song songSettingTemplate) {
        this.originalIntervals = intervals;
        this.originalRythmValues = rythmValues;
        this.originalDurations = durations;
        int numberOfTracks = intervals.size();
        this.firstNotes = new Note[numberOfTracks];
        this.instruments = new int[numberOfTracks];
        this.channels = new int[numberOfTracks];
        this.tempo = songSettingTemplate.getTempo();
        for(int i = 0; i < numberOfTracks; i++) {
            this.firstNotes[i] = new Note(60, 0.5);
            this.instruments[i] = songSettingTemplate.getTrack(i).getInstrument();
            this.channels[i] = songSettingTemplate.getTrack(i).getChannel();
        }
    }

    // public IntervalSong(List<int[]> intervals, List<double[]> durations,
    // Note[] firstNotes, int[] instruments, int[] channels, double tempo) {
    // this.originalIntervals = intervals;
    // this.originalDurations = durations;
    // this.firstNotes = firstNotes;
    // this.
    // }

    public IntervalSong(Song song) {
        this.originalIntervals = new ArrayList<int[]>();
        this.originalRythmValues = new ArrayList<double[]>();
        this.originalDurations = new ArrayList<double[]>();
        Map<Double, List<ComparableNote>> noteMap;
        List<ComparableNote> sortedNoteList = new ArrayList<ComparableNote>();
        Part[] allParts = song.getScore().getPartArray();
        this.firstNotes = new Note[allParts.length];
        this.instruments = new int[allParts.length];
        this.channels = new int[allParts.length];
        this.tempo = song.getTempo();
        Part part = null;

        for (int partIndex = 0; partIndex < allParts.length; partIndex++) {
            part = allParts[partIndex];
            noteMap = new HashMap<Double, List<ComparableNote>>();
            double songLength;
            for (Phrase phrase : part.getPhraseArray()) {
                songLength = phrase.getStartTime();
                for (Note note : phrase.getNoteArray()) {
                    if (!noteMap.containsKey(songLength)) {
                        noteMap.put(songLength, new ArrayList<ComparableNote>());
                    }
                    noteMap.get(songLength).add(
                            new ComparableNote(note, songLength));
                    songLength += note.getRhythmValue();
                }
            }
            // Currently only takes one note at the same time.
            sortedNoteList.clear();
            Iterator<List<ComparableNote>> iterator = noteMap.values()
                    .iterator();
            while (iterator.hasNext()) {
                sortedNoteList.add(iterator.next().get(0));
            }
            Collections.sort(sortedNoteList);

            firstNotes[partIndex] = sortedNoteList.get(0).note;
            instruments[partIndex] = allParts[partIndex].getInstrument();
            channels[partIndex] = allParts[partIndex].getChannel();
            int numberOfNotes = sortedNoteList.size();
            int[] intervals = new int[numberOfNotes - 1];
            double[] rythmValues = new double[numberOfNotes];
            double[] durations = new double[numberOfNotes];
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
            rythmValues[numberOfNotes - 1] = sortedNoteList
                    .get(numberOfNotes - 1).note.getRhythmValue();

            originalIntervals.add(intervals);
            originalRythmValues.add(rythmValues);
            originalDurations.add(durations);
            
        }
    }

    public List<int[]> getIntervals() {
        return originalIntervals;
    }

    public List<double[]> getRythmValues() {
        return originalRythmValues;
    }

    public List<double[]> getDurations() {
        return originalDurations;
    }

    // TODO Better first intervals durations etc.
    public Song toSong() {
        // All arguments must be of the same size/length!
        Score score = new Score();
        Phrase phrase;
        Part newPart;
        int currentPitch;
        int[] currentPartsIntervals;
        double[] currentPartsRythmValues;
        double[] currentPartsDurations;
        Note newNote;
        for (int i = 0; i < originalIntervals.size(); i++) {
            phrase = new Phrase(0.0);
            currentPitch = firstNotes[i].getPitch();
            newNote = new Note(currentPitch, originalRythmValues.get(i)[0]);
            newNote.setDuration(originalDurations.get(i)[0]);
            phrase.add(newNote);
            currentPartsIntervals = originalIntervals.get(i);
            currentPartsRythmValues = originalRythmValues.get(i);
            currentPartsDurations = originalDurations.get(i);
            for (int j = 0; j < currentPartsIntervals.length; j++) {
                currentPitch += currentPartsIntervals[j];
                if(currentPitch < 0) {
                    newNote = new Note(Note.REST, currentPartsRythmValues[j + 1]);
                    
                } else {
                    newNote = new Note(currentPitch, currentPartsRythmValues[j + 1]);
                }
                newNote.setDuration(currentPartsDurations[j + 1]);
                phrase.add(newNote);
            }
            newPart = new Part();
            newPart.add(phrase);
            newPart.setChannel(channels[i]);
            newPart.setInstrument(instruments[i]);
            score.add(newPart);
        }
        score.setTempo(tempo);
        // score.setTimeSignature(song.getScore().getNumerator(),
        // song.getScore().getDenominator());
        return new Song(score);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < originalIntervals.size(); i++) {
            string.append("Track " + i + ": number of notes = "
                    + originalRythmValues.get(i).length + "\n");
            string.append("Intervals: ");
            for (Integer interval : originalIntervals.get(i)) {
                string.append(interval);
                string.append(" ");
            }
            string.append("\nDurations: ");
            for (Double duration : originalRythmValues.get(i)) {
                string.append(duration);
                string.append(" ");
            }
            string.append("\n");
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
