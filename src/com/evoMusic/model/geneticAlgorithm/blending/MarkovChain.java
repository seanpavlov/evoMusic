package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import jm.music.data.Part;
import jm.music.data.Score;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MarkovChain {

    private int numberOfIntervalLookbacks;

    private Random rand;
    private List<ProbabilityMatrix<Integer, Integer>> intervalProbabilityMatrices;
    private List<ProbabilityMatrix<Integer, Double>> rythmValueProbabilityMatrices;
    private List<ProbabilityMatrix<Integer, Double>> durationProbabilityMatrices;
    private List<IntervalSong> intervalledSongs;
    private int numberOfTracks;

    public MarkovChain(int lookbacks, List<Song> songs) {
        if (lookbacks < 0) {
            throw new IllegalArgumentException("Negative lookback value");
        }
        this.numberOfIntervalLookbacks = lookbacks;
        this.rand = new Random();
        // this.realSong = song;
        this.intervalledSongs = new ArrayList<IntervalSong>();
        List<Song> trimmedSongs = trimSongParts(songs);
        for (Song currentSong : trimmedSongs) {
            intervalledSongs.add(new IntervalSong(currentSong));
        }
        numberOfTracks = intervalledSongs.get(0).getTracks().size();
        this.intervalProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Integer>>(
                numberOfTracks);
        this.rythmValueProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Double>>(
                numberOfTracks);
        this.durationProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Double>>(
                numberOfTracks);
        initProbabilityMatrices();
    }

    // TODO Implement and make sure all tracks match each other.
    private List<Song> trimSongParts(List<Song> songList) {
        Set<TrackTag> allTrackTags = new HashSet<TrackTag>();
        List<Song> trimmedSongs = new ArrayList<Song>();
        Iterator<TrackTag> tagIterator;
        TrackTag currentTag;
        // Finding all track tags.
        for (Song song : songList) {
            for (Track track : song.getTracks()) {
                tagIterator = track.getTags().iterator();
                while (tagIterator.hasNext()) {
                    currentTag = tagIterator.next();
                    if (currentTag != TrackTag.NONE) {
                        allTrackTags.add(currentTag);
                    }
                }
            }
        }

        // Adding empty songs.
        for (int i = 0; i < songList.size(); i++) {
            trimmedSongs.add(new Song(new Score()));
        }

        tagIterator = allTrackTags.iterator();
        while (tagIterator.hasNext()) {
            currentTag = tagIterator.next();
            for (int songIndex = 0; songIndex < songList.size(); songIndex++) {
                Song song = songList.get(songIndex);
                boolean foundTrack = false;
                for (Track track : song.getTracks()) {
                    if (track.getTags().contains(currentTag)) {
                        trimmedSongs.get(songIndex).addTrack(track);
                        foundTrack = true;
                    }
                }
                if (!foundTrack) {
                    trimmedSongs.get(songIndex).addTrack(
                            new Track(new Part(), currentTag));
                } else {
                    foundTrack = false;
                }
            }
        }
        return trimmedSongs;

    }

    public Song generateNew() {
        double longestDuration = 0;
        double currentDuration;
        for (IntervalSong currentIntervalSong : intervalledSongs) {
            for (IntervalTrack track : currentIntervalSong.getTracks()) {
                currentDuration = 0;
                for (double duration : track.getDurations()) {
                    currentDuration += duration;
                }
                if (currentDuration > longestDuration) {
                    longestDuration = currentDuration;
                }
            }
        }
        return generateNew(longestDuration);
    }

    public Song generateNew(double maxSongDuration) {
        double trackLength = 0;

        List<Integer> trackIntervals;
        List<Double> trackRythmValues;
        List<Double> trackDurations;

        ProbabilityMatrix<Integer, Integer> trackIntervalMatrix;
        ProbabilityMatrix<Integer, Double> trackRythmMatrix;
        ProbabilityMatrix<Integer, Double> trackDurationMatrix;

        boolean isResting;
        Integer nextInterval;
        double nextRythmValue;
        double nextDuration;
        Vector<Integer> currentSequence;

        IntervalSong newSong = new IntervalSong(getRandomTempo());
        List<int[]> instrumentsAndChannels = getRandomInstrumentsAndChannels();
        int[] firstNotes = getFirstNotes();

        // For each track.
        for (int trackIndex = 0; trackIndex < numberOfTracks; trackIndex++) {
            trackIntervals = new ArrayList<Integer>();
            trackRythmValues = new ArrayList<Double>();
            trackDurations = new ArrayList<Double>();
            trackLength = 0;
            isResting = false;
            trackIntervalMatrix = intervalProbabilityMatrices.get(trackIndex);
            trackRythmMatrix = rythmValueProbabilityMatrices.get(trackIndex);
            trackDurationMatrix = durationProbabilityMatrices.get(trackIndex);

            while (trackLength < maxSongDuration) {
                // Building the track.
                ArrayList<Integer> currentIntervals = new ArrayList<Integer>();
                ArrayList<Double> currentRythmValues = new ArrayList<Double>();
                ArrayList<Double> currentDurations = new ArrayList<Double>();

                for (int intervalIndex = 0; trackLength < maxSongDuration; intervalIndex++) {
                    currentSequence = getNextSequence(intervalIndex,
                            currentIntervals);
                    nextInterval = trackIntervalMatrix.getNext(currentSequence);
                    if (nextInterval == null) {
                        break; // Has come to the end of the song, must start
                               // over.
                    }
                    // Make sure the first interval isn't a restback (high
                    // positive number) or rest.
                    if (intervalIndex == 0) {
                        while (nextInterval > 127 || nextInterval < 0) {
                            nextInterval = trackIntervalMatrix
                                    .getNext(currentSequence);
                        }
                    } else {
                        // Make sure no dubbel rest/restback is added.
                        while (true) {
                            if (nextInterval <= 127 || nextInterval >= 0) {
                                break;
                            }
                            if ((isResting && nextInterval > 127) || !isResting
                                    && nextInterval < 0) {
                                isResting = !isResting;
                                break;
                            }
                            nextInterval = trackIntervalMatrix
                                    .getNext(currentSequence);
                        }
                    }

                    nextRythmValue = trackRythmMatrix.getNext(currentSequence);
                    nextDuration = trackDurationMatrix.getNext(currentSequence);
                    trackLength += nextRythmValue;
                    currentIntervals.add(nextInterval);
                    currentRythmValues.add(nextRythmValue);
                    currentDurations.add(nextDuration);
                }
                trackIntervals.addAll(currentIntervals);
                trackRythmValues.addAll(currentRythmValues);
                trackDurations.addAll(currentDurations);
            }

            // Adding the last rythmValues and durations.
            currentSequence = new Vector<Integer>();
            for (int seqIndex = numberOfIntervalLookbacks; seqIndex > 0; seqIndex--) {
                currentSequence.add(trackIntervals.get(trackIntervals.size()
                        - seqIndex));
            }
            nextRythmValue = trackRythmMatrix.getNext(currentSequence);
            nextDuration = trackDurationMatrix.getNext(currentSequence);
            trackLength += nextRythmValue;
            trackRythmValues.add(nextRythmValue);
            trackDurations.add(nextDuration);

            IntervalTrack iTrack = new IntervalTrack(firstNotes[trackIndex],
                    instrumentsAndChannels.get(0)[trackIndex],
                    instrumentsAndChannels.get(1)[trackIndex],
                    Ints.toArray(trackIntervals),
                    Doubles.toArray(trackRythmValues),
                    Doubles.toArray(trackDurations));
            newSong.addTrack(iTrack);
        }
        return newSong.toSong();
    }

    // TODO implement for real...
    private int[] getFirstNotes() {
        int[] firstNotes = new int[numberOfTracks];
        for (int i = 0; i < numberOfTracks; i++) {
            firstNotes[i] = 60;
            // firstNotes[i] = intervalledSongs.get(0).getTracks().get(i)
            // .getFirstNote();
        }
        return firstNotes;
    }

    // First index is instrument, second is channel. because instrument and
    // channel must match.
    private List<int[]> getRandomInstrumentsAndChannels() {
        int[] instruments = new int[numberOfTracks];
        int[] channels = new int[numberOfTracks];
        List<int[]> instrumentsAndChannels = new ArrayList<int[]>(2);
        double nextRand;
        IntervalTrack track;
        int numberOfSongs = intervalledSongs.size();
        for (int trackIndex = 0; trackIndex < numberOfTracks; trackIndex++) {
            nextRand = rand.nextDouble() * numberOfSongs;
            track = intervalledSongs.get((int) nextRand).getTrack(trackIndex);
            instruments[trackIndex] = track.getInstrument();
            channels[trackIndex] = track.getChannel();
        }
        instrumentsAndChannels.add(instruments);
        instrumentsAndChannels.add(channels);
        return instrumentsAndChannels;

    }

    // TODO Make more random.
    private double getRandomTempo() {
        double tempo = 0;
        for (IntervalSong song : intervalledSongs) {
            tempo += song.getTempo();
        }
        return tempo / intervalledSongs.size();
    }

    private Vector<Integer> getNextSequence(int intervalIndex,
            List<Integer> currentIntervals) {
        // Adding to sequence, wont add more than is available.
        int lookback;
        Vector<Integer> currentSequence = new Vector<Integer>();
        for (int seqIndex = 0; seqIndex < numberOfIntervalLookbacks; seqIndex++) {
            lookback = intervalIndex - (numberOfIntervalLookbacks - seqIndex);
            if (lookback >= 0) {
                currentSequence.add(currentIntervals.get(lookback));

            }
        }
        return currentSequence;
    }

    // private int getPseudoRandomNote(List<IntervalSong> intervalSong, ) {
    // ProbabilityMatrix<Object, Integer> probabilities = new
    // ProbabilityMatrix<Object, Integer>();
    // // Creates a dummy vector.
    // Vector<Object> vec = new Vector<Object>();
    // int newNote = 0;
    // for(Part part : parts) {
    // for(Phrase phrases : part.getPhraseArray()) {
    // for(Note currentNote : phrases.getNoteArray()) {
    // probabilities.addCount(vec, currentNote.getPitch());
    // }
    // }
    // }
    // probabilities.initProbabilies();
    // do {
    // newNote = probabilities.getNext(vec);
    // } while (newNote == Note.REST);
    // return newNote;
    // }

    private void initProbabilityMatrices() {
        int[] currentIntervals;
        double[] currentRythmValues;
        double[] currentDurations;
        IntervalTrack currentTrack;

        ProbabilityMatrix<Integer, Integer> currentIntervalMatrix;
        ProbabilityMatrix<Integer, Double> currentRythmValueMatrix;
        ProbabilityMatrix<Integer, Double> currentDurationMatrix;

        Vector<Integer> sequence;
        int currentIntervalsLength;

        for (IntervalSong currentIntervalSong : intervalledSongs) {
            // for each part
            for (int partIndex = 0; partIndex < numberOfTracks; partIndex++) {
                currentTrack = currentIntervalSong.getTrack(partIndex);
                currentIntervals = currentTrack.getIntervals();
                currentRythmValues = currentTrack.getRythmValues();
                currentDurations = currentTrack.getDurations();

                if (intervalProbabilityMatrices.size() == partIndex) {
                    intervalProbabilityMatrices
                            .add(new ProbabilityMatrix<Integer, Integer>());
                    rythmValueProbabilityMatrices
                            .add(new ProbabilityMatrix<Integer, Double>());
                    durationProbabilityMatrices
                            .add(new ProbabilityMatrix<Integer, Double>());
                }
                currentIntervalMatrix = intervalProbabilityMatrices
                        .get(partIndex);
                currentRythmValueMatrix = rythmValueProbabilityMatrices
                        .get(partIndex);
                currentDurationMatrix = durationProbabilityMatrices
                        .get(partIndex);

                currentIntervalsLength = currentIntervals.length;

                for (int i = 0; i < currentIntervalsLength
                        - (numberOfIntervalLookbacks); i++) {
                    // Adding for empty sequences.
                    sequence = new Vector<Integer>();
                    currentIntervalMatrix.addCount(sequence,
                            currentIntervals[i]);
                    currentRythmValueMatrix.addCount(sequence,
                            currentRythmValues[i]);
                    currentDurationMatrix.addCount(sequence,
                            currentDurations[i]);
                    // Adding for longer sequences.
                    for (int j = i; j < i + numberOfIntervalLookbacks; j++) {
                        sequence = new Vector<Integer>(sequence);
                        sequence.add(currentIntervals[j]);
                        currentIntervalMatrix.addCount(sequence,
                                currentIntervals[j + 1]);
                        currentRythmValueMatrix.addCount(sequence,
                                currentRythmValues[j + 1]);
                        currentDurationMatrix.addCount(sequence,
                                currentDurations[j + 1]);
                    }

                }
                sequence = new Vector<Integer>();
                for (int i = numberOfIntervalLookbacks; i > 0; i--) {
                    sequence.add(currentIntervalsLength - i);
                }
                currentRythmValueMatrix.addCount(sequence,
                        currentRythmValues[currentRythmValues.length - 1]);
                currentDurationMatrix.addCount(sequence,
                        currentDurations[currentDurations.length - 1]);
            }
        }
        for (int i = 0; i < numberOfTracks; i++) {
            intervalProbabilityMatrices.get(i).initProbabilies();
            rythmValueProbabilityMatrices.get(i).initProbabilies();
            durationProbabilityMatrices.get(i).initProbabilies();
        }

    }
}
