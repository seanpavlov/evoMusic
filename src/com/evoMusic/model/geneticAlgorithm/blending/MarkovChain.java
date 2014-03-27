package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MarkovChain {

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

        int numberOfTracks = originalSong.getIntervals().size();

        List<int[]> intervals = new ArrayList<int[]>();
        List<double[]> rythmValues = new ArrayList<double[]>();
        List<double[]> durations = new ArrayList<double[]>();
        int[] firstNotes = new int[numberOfTracks];

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
            currentIntervalMatrix = intervalProbabilityMatrices.get(trackIndex);
            currentRythmMatrix = rythmValueProbabilityMatrices.get(trackIndex);
            currentDurationMatrix = durationProbabilityMatrices.get(trackIndex);

            int lookback;
            // TODO make sequences that overlap the end and beginning maybe.
            for (int intervalIndex = 0; trackLength < maxSongDuration; intervalIndex++) {
                currentSequence = new Vector<Integer>();
                // Adding to sequence, wont add more than is available.
                for (int seqIndex = 0; seqIndex < numberOfIntervalLookbacks; seqIndex++) {
                    lookback = intervalIndex
                            - (numberOfIntervalLookbacks - seqIndex);
                    if (lookback >= 0) {
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
            // Adding the last rythmValues and durations.
            currentSequence = new Vector<Integer>();
            for (int seqIndex = numberOfIntervalLookbacks; seqIndex > 0; seqIndex--) {
                currentSequence.add(currentIntervals.get(currentIntervals
                        .size() - seqIndex));
            }
            nextRythmValue = currentRythmMatrix.getNext(currentSequence);
            nextDuration = currentDurationMatrix.getNext(currentSequence);
            trackLength += nextRythmValue;
            currentRythmValues.add(nextRythmValue);
            currentDurations.add(nextDuration);

            intervals.add(Ints.toArray(currentIntervals));
            rythmValues.add(Doubles.toArray(currentRythmValues));
            durations.add(Doubles.toArray(currentDurations));

            // Adding the first notes.
            firstNotes[trackIndex] = getPseudoRandomNote(realSong.getTrack(trackIndex));
        }
        return new IntervalSong(intervals, rythmValues, durations, realSong, firstNotes)
                .toSong();
    }

    private int getPseudoRandomNote(Part part) {
        ProbabilityMatrix<Object, Integer> probabilities = new ProbabilityMatrix<Object, Integer>();
        // Creates a dummy vector.
        Vector<Object> vec = new Vector<Object>();
        int newNote = 0;
        for(Phrase phrases : part.getPhraseArray()) {
            for(Note currentNote : phrases.getNoteArray()) {
                probabilities.addCount(vec, currentNote.getPitch());
            }
        }
        probabilities.initProbabilies();
        do {
            newNote = probabilities.getNext(vec);
        } while (newNote == Note.REST);
        return newNote;
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
                    - (numberOfIntervalLookbacks); i++) {
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
