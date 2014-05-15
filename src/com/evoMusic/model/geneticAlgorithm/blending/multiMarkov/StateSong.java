package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

public class StateSong {

    public static final int MODULATED_REST = -1000000;

    private double tempo;
    private int[] firstNotes;
    private int numberOfTracks;
    private List<TrackProperties> trackProperties;

    private List<TempState<Integer>> intervalStates;
    private List<TempState<Double>> rhythmValueStates;
    private List<TempState<Double>> durationStates;
    private List<TempState<Double>> timeDeltaStates;
    private List<TempState<Integer>> dynamicStates;

    /**
     * Creates a new state song from a given song, maintaining its
     * properties.
     * 
     * @param song
     *            The song from which this instance will be based on.
     */
    public StateSong(Song song) {
        this.tempo = song.getTempo();
        this.trackProperties = new ArrayList<TrackProperties>();
        this.numberOfTracks = song.getNbrOfTracks();
        this.firstNotes = new int[numberOfTracks];
        Arrays.fill(firstNotes, Integer.MAX_VALUE);

        this.intervalStates = new ArrayList<TempState<Integer>>();
        this.rhythmValueStates = new ArrayList<TempState<Double>>();
        this.durationStates = new ArrayList<TempState<Double>>();
        this.timeDeltaStates = new ArrayList<TempState<Double>>();
        this.dynamicStates = new ArrayList<TempState<Integer>>();

        // sort by start time and group notes at the same start time.
        Part currentPart;
        double partLength;
        List<SortNote> sortNotes = new ArrayList<SortNote>();
        // TempState currentState;
        for (int trackIndex = 0; trackIndex < numberOfTracks; trackIndex++) {
            currentPart = song.getTrack(trackIndex).getPart();
            trackProperties.add(new TrackProperties(song.getTrack(trackIndex)));
            for (Phrase phrase : currentPart.getPhraseArray()) {
                partLength = phrase.getStartTime();
                for (Note note : phrase.getNoteArray()) {
                    sortNotes.add(new SortNote(note, partLength, trackIndex));
                    partLength += note.getRhythmValue();
                }
            }
        }
        Collections.sort(sortNotes);

        List<SortNote> notesToBeAdded = new ArrayList<SortNote>();
        // Find the first notes.
        for (SortNote note : sortNotes) {
            // if it is the first note.
            if (firstNotes[note.trackIndex] == Integer.MAX_VALUE) {
                // if there should be a rest before that.
                if (!(Math.abs(note.startTime) < SortNote.TIME_PRECISION)) {
                    notesToBeAdded.add(new SortNote(new Note(Note.REST,
                            note.startTime), 0.0, note.trackIndex));
                    firstNotes[note.trackIndex] = MODULATED_REST;
                } else {
                    firstNotes[note.trackIndex] = formatPitch(note.note
                            .getPitch());
                }
            }
        }
        sortNotes.addAll(notesToBeAdded);
        Collections.sort(sortNotes);

        // Initialize all states and find the largest state size.
        int[] previousPitches = Arrays.copyOf(firstNotes, firstNotes.length);
        double previousStartTime = 0.0;
        int currentTrack;

        // Initiate all states.
        for (SortNote sortNote : sortNotes) {
            currentTrack = sortNote.trackIndex;

            intervalStates
                    .add(new TempState<Integer>(formatPitch(sortNote.note
                            .getPitch()) - previousPitches[currentTrack],
                            currentTrack));

            rhythmValueStates.add(new TempState<Double>(sortNote.note
                    .getRhythmValue(), currentTrack));

            durationStates.add(new TempState<Double>(sortNote.note
                    .getDuration(), currentTrack));

            timeDeltaStates.add(new TempState<Double>(sortNote.startTime
                    - previousStartTime, currentTrack));

            dynamicStates.add(new TempState<Integer>(
                    sortNote.note.getDynamic(), currentTrack));

            previousStartTime = sortNote.startTime;
            previousPitches[currentTrack] = formatPitch(sortNote.note
                    .getPitch());
        }
    }

    private int formatPitch(int pitch) {
        if (pitch == Note.REST) {
            return MODULATED_REST;
        }
        return pitch;
    }

    /**
     * Creates a new interval track from the given properties.
     * 
     */
    public StateSong(double tempo, int[] firstNotes,
            List<TrackProperties> trackProperties, int numberOfTracks,
            List<TempState<Integer>> intervalStates,
            List<TempState<Double>> rhythmValueStates,
            List<TempState<Double>> durationStates,
            List<TempState<Double>> timeDeltaStates,
            List<TempState<Integer>> dynamicStates) {

        this.tempo = tempo;
        this.firstNotes = firstNotes;
        this.numberOfTracks = numberOfTracks;
        this.intervalStates = intervalStates;
        this.rhythmValueStates = rhythmValueStates;
        this.durationStates = durationStates;
        this.timeDeltaStates = timeDeltaStates;
        this.dynamicStates = dynamicStates;
    }

    /**
     * Creates Song object converted from this object.
     * 
     * @return The song made from this object's properties.
     */
    public Song toSong() {
        List<Part> parts = new ArrayList<Part>(numberOfTracks);
        for (int i = 0; i < numberOfTracks; i++) {
            parts.add(new Part());
        }
        int[] previousPitches = Arrays.copyOf(firstNotes, numberOfTracks);
        Part currentPart;
        int currentTrackIndex;
        int currentPitch;
        double currentTime = 0.0;
        Note newNote;

        // looping through each chord.
        for (int stateIndex = 0; stateIndex < intervalStates.size(); stateIndex++) {
            currentTrackIndex = intervalStates.get(stateIndex).trackIndex();
            currentPart = parts.get(currentTrackIndex);
            currentPitch = previousPitches[currentTrackIndex]
                    + intervalStates.get(stateIndex).getValue();
            previousPitches[currentTrackIndex] = currentPitch;
            currentTime += timeDeltaStates.get(stateIndex).getValue();

            if (currentPitch < Note.MIN_PITCH || currentPitch > Note.MAX_PITCH) {
                newNote = new Note(Note.REST, rhythmValueStates.get(stateIndex)
                        .getValue());
            } else {
                newNote = new Note(currentPitch, rhythmValueStates.get(
                        stateIndex).getValue());
            }
            newNote.setDuration(durationStates.get(stateIndex).getValue());
            newNote.setDynamic(dynamicStates.get(stateIndex).getValue());
            currentPart.add(new Phrase(newNote, currentTime));
        }
        Score score = new Score(this.tempo);
        Song song = new Song(score);
        TrackProperties currentTP;
        for (int trackIndex = 0; trackIndex < numberOfTracks; trackIndex++) {
            currentTP = trackProperties.get(trackIndex);
            parts.get(trackIndex).setChannel(currentTP.channel);
            parts.get(trackIndex).setInstrument(currentTP.instrument);
            song.addTrack(new Track(parts.get(trackIndex), currentTP.tag));
        }
        return song;
    }

    /**
     * Gets the first notes of this song.
     * 
     * @return The first notes of this song.
     */
    public int[] getFirstNotes() {
        return firstNotes;
    }
    
    public int numberOfTracks() {
        return numberOfTracks;
    }
    
    public double getTempo() {
        return this.tempo;
    }

    /**
     * Gets the track properties of this song.
     * 
     * @return The track properties of this song.
     */
    public List<TrackProperties> getTrackProperties() {
        return trackProperties;
    }

    public List<TempState<Integer>> getIntervals() {
        return this.intervalStates;
    }
    
    public List<TempState<Double>> getRhythmValues() {
        return this.rhythmValueStates;
    }
    
    public List<TempState<Double>> getDurations() {
        return this.durationStates;
    }
    
    public List<TempState<Integer>> getDynamics() {
        return this.dynamicStates;
    }
    
    public List<TempState<Double>> getTimeDeltas() {
        return this.timeDeltaStates;
    }
    
    private class SortNote implements Comparable<SortNote> {

        public static final double TIME_PRECISION = 0.000001;

        private Note note;
        private double startTime;
        private int trackIndex;

        public SortNote(Note note, double startTime, int trackIndex) {
            this.note = note;
            this.startTime = startTime;
            this.trackIndex = trackIndex;
        }

        @Override
        public int compareTo(SortNote o) {
            if (Math.abs(this.startTime - o.startTime) < TIME_PRECISION) {
                return 0;
            } else if (this.startTime < o.startTime) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    public class TrackProperties {

        protected int instrument;
        protected int channel;
        protected TrackTag tag;
        protected boolean isEmpty;

        public TrackProperties(Track track) {
            this.instrument = track.getPart().getInstrument();
            this.channel = track.getPart().getChannel();
            this.tag = track.getTag();
            this.isEmpty = track.getPart().size() == 0;
        }
    }
}
