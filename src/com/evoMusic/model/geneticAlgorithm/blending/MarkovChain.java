package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

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
    
    private Random rand;
    private List<ProbabilityMatrix<Integer, Integer>> intervalProbabilityMatrices;
    private List<ProbabilityMatrix<Integer, Double>> rythmValueProbabilityMatrices;
    private List<ProbabilityMatrix<Integer, Double>> durationProbabilityMatrices;
    private IntervalSong originalSong;

    public MarkovChain(Song song) {
        this.originalSong = new IntervalSong(song);
        this.intervalProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Integer>>();
        this.rythmValueProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Double>>();
        this.durationProbabilityMatrices = new ArrayList<ProbabilityMatrix<Integer, Double>>();
        initProbabilityMatrices();
    }

    public Song generateNew() {
        double longestDuration = 0;
        double currentDuration;
        for(double[] durations : originalSong.getDurations()) {
            currentDuration = 0;
            for(double duration : durations) {
                currentDuration += duration;
            }
            if(currentDuration > longestDuration) {
                longestDuration = currentDuration;
            }
        }
        return generateNew(longestDuration);
    }

    public Song generateNew(double maxSongDuration) {
        
        return null;
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
        for(int partIndex = 0; partIndex < originalSong.getIntervals().size(); partIndex++) {
            currentIntervals = originalSong.getIntervals().get(partIndex);
            currentRythmValues = originalSong.getRythmValues().get(partIndex);
            currentDurations = originalSong.getDurations().get(partIndex);
            
            currentIntervalMatrix = new ProbabilityMatrix<Integer, Integer>();
            currentRythmValueMatrix = new ProbabilityMatrix<Integer, Double>();
            currentDurationMatrix = new ProbabilityMatrix<Integer, Double>();
            
            currentIntervalsLength = currentIntervals.length;
            
            for(int i = 0; i < currentIntervalsLength - (numberOfIntervalLookbacks + 1); i++) {
                sequence = new Vector<Integer>();
                for(int j = i; j < i + numberOfIntervalLookbacks; j++) {
                    sequence.add(currentIntervals[j]);
                    currentIntervalMatrix.addCount(sequence, currentIntervals[j+1]);
                    currentRythmValueMatrix.addCount(sequence, currentRythmValues[j+1]);
                    currentDurationMatrix.addCount(sequence, currentDurations[j+1]);
                }
                
            }
            sequence = new Vector<Integer>();
            for(int i = numberOfIntervalLookbacks; i > 0; i--) {
                sequence.add(currentIntervalsLength-i);
            }
            currentRythmValueMatrix.addCount(sequence, currentRythmValues[currentRythmValues.length - 1]);
            currentDurationMatrix.addCount(sequence, currentDurations[currentDurations.length - 1]);
            
            intervalProbabilityMatrices.add(currentIntervalMatrix);
            rythmValueProbabilityMatrices.add(currentRythmValueMatrix);
            durationProbabilityMatrices.add(currentDurationMatrix);
        }

        
    }
    
    private int selectNextInterval(int[] pattern, List<Integer> intervalList) {
        List<Integer> foundIntervals = new ArrayList<Integer>();
        List<Integer> timesFound = new ArrayList<Integer>();
        boolean foundMatchingPattern;
        for(int i = 0; i < intervalList.size() - pattern.length; i++) {
            foundMatchingPattern = true;
            for(int j = 0; j < pattern.length; j++) {
                if(!(intervalList.get(i + j) == pattern[j])) {
                    foundMatchingPattern = false;
                    break;
                }
            }
            if(foundMatchingPattern) {
                // TODO Add interval to lists or increment its count
            }
        }
        
        
        
        return 0;
    }
    
    private class ProbabilityMatrix <E, T> {
        
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
            if(!sequences.contains(sequence)) {
                sequences.add(sequence);
            }
            sequenceIndex = sequences.indexOf(sequence);
            if(!intervals.contains(interval)) {
                intervals.add(interval);
            }
            intervalIndex = intervals.indexOf(interval);
            if(occurences.get(sequenceIndex) == null) {
                occurences.add(new ArrayList<Integer>());
            }
            if(occurences.get(sequenceIndex).get(intervalIndex) == null) {
                occurences.get(sequenceIndex).add(1);
            } else {
                occurences.get(sequenceIndex).set(intervalIndex, occurences.get(sequenceIndex).get(intervalIndex)+1);
            }
        }
        
        public void initProbabilies() {
            probabilities = new ArrayList<List<Double>>();
            int sum;
            List<Integer> currentSeq;
            for(int seqIndex = 0; seqIndex < occurences.size(); seqIndex++) {
                probabilities.add(new ArrayList<Double>());
                currentSeq = occurences.get(seqIndex);
                sum = 0;
                for(Integer count : currentSeq) {
                    sum += count;
                    probabilities.get(seqIndex).add(count.doubleValue());
                }
                for(Double currentProb : probabilities.get(seqIndex)) {
                    currentProb = currentProb / sum;
                }
            }
        }
        
        public List<Vector<E>> getSequenceList() {
            return sequences;
        }
        
        public List<T> getIntervalList() {
            return intervals;
        }
        
        public List<List<Double>> getProbabilities() {
            if(probabilities == null) {
                initProbabilies();
            }
            return probabilities;
        }
    }
}
