package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.evoMusic.model.Song;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MarkovChain {

    /**
     * The number of decimals kept in timeDiff.
     */
    private static final int timeDiffAccuracy = 10;
    private static final int numberOfIntervalLookbacks = 3;

    private Random rand;
    private List<ProbabilityMatrix<Integer, Integer>> intervalProbabilityMatrices;
    private List<ProbabilityMatrix<Integer, Double>> rythmValueProbabilityMatrices;
    private List<ProbabilityMatrix<Integer, Double>> durationProbabilityMatrices;
    private IntervalSong originalSong;
    private Song realSong;

    public MarkovChain(Song song) {
        this.rand = new Random();
        this.realSong = song;
        this.originalSong = new IntervalSong(song);
        this.intervalProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Integer>>();
        this.rythmValueProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Double>>();
        this.durationProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Double>>();
        initProbabilityMatrices();
    }

    public Song generateNew() {
        double longestDuration = 0;
        double currentDuration;
        for (double[] durations : originalSong.getDurations()) {
            currentDuration = 0;
            for (double duration : durations) {
                currentDuration += duration;
            }
            if (currentDuration > longestDuration) {
                longestDuration = currentDuration;
            }
        }
        return generateNew(longestDuration);
    }

    public Song generateNew(double maxSongDuration) {
        double trackLength = 0;

        List<int[]> intervals = new ArrayList<int[]>();
        List<double[]> rythmValues = new ArrayList<double[]>();
        List<double[]> durations = new ArrayList<double[]>();

        int numberOfTracks = originalSong.getIntervals().size();

        List<Integer> currentIntervals;
        List<Double> currentRythmValues;
        List<Double> currentDurations;

        ProbabilityMatrix<Integer, Integer> currentIntervalMatrix;
        ProbabilityMatrix<Integer, Double> currentRythmMatrix;
        ProbabilityMatrix<Integer, Double> currentDurationMatrix;

        int nextInterval;
        double nextRythmValue;
        double nextDuration;
        Vector<Integer> currentSequence;
        

        // For each track.
        for (int trackIndex = 0; trackIndex < numberOfTracks; trackIndex++) {
            currentIntervals = new ArrayList<Integer>();
            currentRythmValues = new ArrayList<Double>();
            currentDurations = new ArrayList<Double>();
            trackLength = 0;
            currentIntervalMatrix = intervalProbabilityMatrices
                    .get(trackIndex);
            currentRythmMatrix = rythmValueProbabilityMatrices
                    .get(trackIndex);
            currentDurationMatrix = durationProbabilityMatrices
                    .get(trackIndex);
            
            int lookback;
            for (int intervalIndex = 0; trackLength < maxSongDuration; intervalIndex++) {
                currentSequence = new Vector<Integer>();
                // Adding to sequence, wont add more than is available.
                for (int seqIndex = 0; seqIndex < numberOfIntervalLookbacks
                        && seqIndex < intervalIndex; seqIndex++) {
                    lookback = intervalIndex - (numberOfIntervalLookbacks - seqIndex);
                    if(lookback >= 0) {
                        currentSequence.add(currentIntervals.get(lookback));
                        
                    }
                }
                
                nextInterval = currentIntervalMatrix.getNext(currentSequence);
                nextRythmValue = currentRythmMatrix.getNext(currentSequence);
                nextDuration = currentDurationMatrix.getNext(currentSequence);
                trackLength += nextRythmValue;
                currentIntervals.add(nextInterval);
                currentRythmValues.add(nextRythmValue);
                currentDurations.add(nextDuration);
            }

            intervals.add(Ints.toArray(currentIntervals));
            rythmValues.add(Doubles.toArray(currentRythmValues));
            durations.add(Doubles.toArray(currentDurations));
        }
        return new IntervalSong(intervals, rythmValues, durations, realSong).toSong();
    }

    private void initProbabilityMatrices() {
        int[] currentIntervals;
        double[] currentRythmValues;
        double[] currentDurations;

        ProbabilityMatrix<Integer, Integer> currentIntervalMatrix;
        ProbabilityMatrix<Integer, Double> currentRythmValueMatrix;
        ProbabilityMatrix<Integer, Double> currentDurationMatrix;

        Vector<Integer> sequence;
        int currentIntervalsLength;

        // for each part
        for (int partIndex = 0; partIndex < originalSong.getIntervals().size(); partIndex++) {
            currentIntervals = originalSong.getIntervals().get(partIndex);
            currentRythmValues = originalSong.getRythmValues().get(partIndex);
            currentDurations = originalSong.getDurations().get(partIndex);

            currentIntervalMatrix = new ProbabilityMatrix<Integer, Integer>();
            currentRythmValueMatrix = new ProbabilityMatrix<Integer, Double>();
            currentDurationMatrix = new ProbabilityMatrix<Integer, Double>();

            currentIntervalsLength = currentIntervals.length;

            for (int i = 0; i < currentIntervalsLength
                    - (numberOfIntervalLookbacks + 1); i++) {
                // Adding for empty sequences.
                sequence = new Vector<Integer>();
                currentIntervalMatrix.addCount(sequence, currentIntervals[i]);
                currentRythmValueMatrix.addCount(sequence,
                        currentRythmValues[i]);
                currentDurationMatrix.addCount(sequence, currentDurations[i]);
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

            currentIntervalMatrix.initProbabilies();
            currentRythmValueMatrix.initProbabilies();
            currentDurationMatrix.initProbabilies();

            intervalProbabilityMatrices.add(currentIntervalMatrix);
            rythmValueProbabilityMatrices.add(currentRythmValueMatrix);
            durationProbabilityMatrices.add(currentDurationMatrix);
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
            if (occurences.get(sequenceIndex).size() <= intervalIndex) {
                occurences.get(sequenceIndex).add(1);
            } else {
                occurences.get(sequenceIndex).set(intervalIndex,
                        occurences.get(sequenceIndex).get(intervalIndex) + 1);
            }
        }

        public void initProbabilies() {
            probabilities = new ArrayList<List<Double>>();
            int sum;
            List<Integer> currentSeq;
            for (int seqIndex = 0; seqIndex < occurences.size(); seqIndex++) {
                probabilities.add(new ArrayList<Double>());
                currentSeq = occurences.get(seqIndex);
                sum = 0;
                for (Integer count : currentSeq) {
                    sum += count;
                    probabilities.get(seqIndex).add(count.doubleValue());
                }
                for (Double currentProb : probabilities.get(seqIndex)) {
                    currentProb = currentProb / sum;
                }
            }
        }
        
        public T getNext(Vector<E> sequence) {
            int sequenceIndex = sequences.indexOf(sequence);
            if(probabilities == null) {
                initProbabilies();
            }
            List<Double> currentProbabilities = probabilities.get(sequenceIndex);
            double currentProbability = 0;
            double randomValue = rand.nextDouble();
            int objectIndex = -1;
            do {
                objectIndex += 1;
                currentProbability += currentProbabilities.get(objectIndex);
            }
            while (currentProbability < randomValue);
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
