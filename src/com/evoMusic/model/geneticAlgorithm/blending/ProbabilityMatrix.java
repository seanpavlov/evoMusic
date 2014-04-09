package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class ProbabilityMatrix<E, T> {

    private Random rand;
    private List<Vector<E>> sequences;
    private List<T> intervals;
    // First index is sequence, second is interval.
    private List<List<Integer>> occurences;
    private List<List<Double>> probabilities;

    public ProbabilityMatrix() {
        sequences = new ArrayList<Vector<E>>();
        intervals = new ArrayList<T>();
        occurences = new ArrayList<List<Integer>>();
        rand = new Random();
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
