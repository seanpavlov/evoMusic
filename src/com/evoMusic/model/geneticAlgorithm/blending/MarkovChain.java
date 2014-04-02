package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.evoMusic.model.Song;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MarkovChain {

    private int numberOfIntervalLookbacks;

    private Random rand;
    private List<ProbabilityMatrix<Integer, Integer>> intervalProbabilityMatrices;
    private List<ProbabilityMatrix<Integer, Double>> rythmValueProbabilityMatrices;
    private List<ProbabilityMatrix<Integer, Double>> durationProbabilityMatrices;
    private List<IntervalSong> intervalledSongs;
    // private Song realSong;
    // private List<Song> originalSongs;
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
        numberOfTracks = intervalledSongs.get(0).getIntervals().size();
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
        return songList;

    }

    public Song generateNew() {
        double longestDuration = 0;
        double currentDuration;
        for (IntervalSong currentIntervalSong : intervalledSongs) {
            for (double[] durations : currentIntervalSong.getDurations()) {
                currentDuration = 0;
                for (double duration : durations) {
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

        List<int[]> intervals = new ArrayList<int[]>();
        List<double[]> rythmValues = new ArrayList<double[]>();
        List<double[]> durations = new ArrayList<double[]>();

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

            intervals.add(Ints.toArray(trackIntervals));
            rythmValues.add(Doubles.toArray(trackRythmValues));
            durations.add(Doubles.toArray(trackDurations));

            // Adding the first notes.
            // TODO Find better (any) way to get first notes.
            // firstNotes[trackIndex] = getPseudoRandomNote(intervalledSongs);
        }
        List<int[]> instrumentsAndChannels = getRandomInstrumentsAndChannels();
        IntervalSong newSong = new IntervalSong(intervals, rythmValues,
                durations, instrumentsAndChannels.get(0),
                instrumentsAndChannels.get(1), getRandomTempo(),
                getFirstNotes());
        return newSong.toSong();
    }

    // TODO implement for real...
    private int[] getFirstNotes() {
        return intervalledSongs.get(0).getFirstNotes();
    }

    // First index is instrument, second is channel. because instrument and
    // channel must match.
    private List<int[]> getRandomInstrumentsAndChannels() {
        int[] instruments = new int[numberOfTracks];
        int[] channels = new int[numberOfTracks];
        List<int[]> instrumentsAndChannels = new ArrayList<int[]>(2);
        double nextRand;
        int numberOfSongs = intervalledSongs.size();
        for(int trackIndex = 0; trackIndex < numberOfTracks; trackIndex++) {
            nextRand = rand.nextDouble()*numberOfSongs;
            instruments[trackIndex] = intervalledSongs.get((int)nextRand).getInstruments()[trackIndex];
            channels[trackIndex] = intervalledSongs.get((int)nextRand).getChannels()[trackIndex];
        }
        instrumentsAndChannels.add(instruments);
        instrumentsAndChannels.add(channels);
        return instrumentsAndChannels;

    }

    // TODO Make more random.
    private double getRandomTempo() {
        double tempo = 0;
        for(IntervalSong song : intervalledSongs) {
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

        ProbabilityMatrix<Integer, Integer> currentIntervalMatrix;
        ProbabilityMatrix<Integer, Double> currentRythmValueMatrix;
        ProbabilityMatrix<Integer, Double> currentDurationMatrix;

        Vector<Integer> sequence;
        int currentIntervalsLength;

        for (IntervalSong currentIntervalSong : intervalledSongs) {
            // for each part
            for (int partIndex = 0; partIndex < currentIntervalSong
                    .getIntervals().size(); partIndex++) {
                currentIntervals = currentIntervalSong.getIntervals().get(
                        partIndex);
                currentRythmValues = currentIntervalSong.getRythmValues().get(
                        partIndex);
                currentDurations = currentIntervalSong.getDurations().get(
                        partIndex);

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

    private class ProbabilityMatrix<E, T> {

        private List<Vector<E>> sequences;
        private List<T> intervals;
        // First index is sequence, second is interval.
        private List<List<Integer>> occurences;
        private List<List<Double>> probabilities;

        public ProbabilityMatrix() {
            sequences = new ArrayList<Vector<E>>();
            intervals = new ArrayList<T>();
            occurences = new ArrayList<List<Integer>>();
        }

        public void addCount(Vector<E> sequence, T interval) {
            int sequenceIndex;
            int intervalIndex;
            List<Integer> thisOccurence;
            if (!sequences.contains(sequence)) {
                sequences.add(sequence);
            }
            sequenceIndex = sequences.indexOf(sequence);
            if (!intervals.contains(interval)) {
                intervals.add(interval);
            }
            intervalIndex = intervals.indexOf(interval);
            if (occurences.size() <= sequenceIndex) {
                occurences.add(new ArrayList<Integer>());
            }
            thisOccurence = occurences.get(sequenceIndex);
            while (thisOccurence.size() <= intervalIndex) {
                thisOccurence.add(0);
            }
            thisOccurence.set(intervalIndex,
                    thisOccurence.get(intervalIndex) + 1);
        }

        public void initProbabilies() {
            probabilities = new ArrayList<List<Double>>();
            int sum;
            List<Integer> currentSeq;
            List<Double> currentProbs;
            for (int seqIndex = 0; seqIndex < occurences.size(); seqIndex++) {
                currentProbs = new ArrayList<Double>();
                probabilities.add(currentProbs);
                currentSeq = occurences.get(seqIndex);
                sum = 0;
                for (Integer count : currentSeq) {
                    sum += count;
                    currentProbs.add(count.doubleValue());
                }
                for (int probIndex = 0; probIndex < currentProbs.size(); probIndex++) {
                    currentProbs.set(probIndex, currentProbs.get(probIndex)
                            / sum);
                }
            }
        }

        public T getNext(Vector<E> sequence) {
            int sequenceIndex = sequences.indexOf(sequence);
            if (sequenceIndex == -1) {
                return null;
            }
            if (probabilities == null) {
                initProbabilies();
            }

            List<Double> currentProbabilities = probabilities
                    .get(sequenceIndex);
            double currentProbability = 0;
            double randomValue = rand.nextDouble();
            int objectIndex = -1;
            do {
                objectIndex += 1;
                currentProbability += currentProbabilities.get(objectIndex);
            } while (currentProbability < randomValue);
            return intervals.get(objectIndex);
        }

        public List<Vector<E>> getSequenceList() {
            return sequences;
        }

        public List<T> getIntervalList() {
            return intervals;
        }

        public List<List<Double>> getProbabilities() {
            if (probabilities == null) {
                initProbabilies();
            }
            return probabilities;
        }
    }
}
