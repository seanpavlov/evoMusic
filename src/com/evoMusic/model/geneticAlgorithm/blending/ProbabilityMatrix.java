package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class ProbabilityMatrix<E, T> {

    private Random rand;
    private List<Vector<E>> sequences;
    private List<T> values;
    // First index is sequence, second is interval.
    private List<List<Integer>> occurrences;
    private List<List<Double>> probabilities;

    /**
     * Creates a probability matrix of the given types. E is the type of the
     * values in the sequences to track. T is the type of values that follows
     * the sequences.
     */
    public ProbabilityMatrix() {
        sequences = new ArrayList<Vector<E>>();
        values = new ArrayList<T>();
        occurrences = new ArrayList<List<Integer>>();
        rand = new Random();
    }

    /**
     * Adds a count to that the given value follows the given sequence.
     * 
     * @param sequence
     *            The previous sequence.
     * @param value
     *            The value that follows.
     */
    public void addCount(Vector<E> sequence, T value) {
        int sequenceIndex;
        int valueIndex;
        List<Integer> thisOccurence;
        if (!sequences.contains(sequence)) {
            sequences.add(sequence);
        }
        sequenceIndex = sequences.indexOf(sequence);
        if (!values.contains(value)) {
            values.add(value);
        }
        valueIndex = values.indexOf(value);
        if (occurrences.size() <= sequenceIndex) {
            occurrences.add(new ArrayList<Integer>());
        }
        thisOccurence = occurrences.get(sequenceIndex);
        while (thisOccurence.size() <= valueIndex) {
            thisOccurence.add(0);
        }
        thisOccurence.set(valueIndex, thisOccurence.get(valueIndex) + 1);
    }

    /**
     * Initiates the probabilities given the counted occurrences. If one wants
     * to update the probabilities and has called addCount after last call to
     * this method, this method must be called again.
     */
    public void initProbabilies() {
        probabilities = new ArrayList<List<Double>>();
        int sum;
        List<Integer> currentSeq;
        List<Double> currentProbs;
        for (int seqIndex = 0; seqIndex < occurrences.size(); seqIndex++) {
            currentProbs = new ArrayList<Double>();
            probabilities.add(currentProbs);
            currentSeq = occurrences.get(seqIndex);
            sum = 0;
            for (Integer count : currentSeq) {
                sum += count;
                currentProbs.add(count.doubleValue());
            }
            for (int probIndex = 0; probIndex < currentProbs.size(); probIndex++) {
                currentProbs.set(probIndex, currentProbs.get(probIndex) / sum);
            }
        }
    }

    /**
     * Given a sequence, returns the next element randomly, taking into
     * consideration the number of the added counts. If there is no possible
     * element that follows sequence, this method will return null.
     * 
     * @param sequence
     *            The sequence of which the returned element must follow.
     * @return An element that is probable to follow the sequence. Null if
     *         sequence has no elements that follows it.
     */
    public T getNext(Vector<E> sequence) {
        int sequenceIndex = sequences.indexOf(sequence);
        if (sequenceIndex == -1) {
            return null;
        }
        if (probabilities == null) {
            initProbabilies();
        }

        List<Double> currentProbabilities = probabilities.get(sequenceIndex);
        double currentProbability = 0;
        double randomDouble = rand.nextDouble();
        int objectIndex = -1;
        do {
            objectIndex += 1;
            currentProbability += currentProbabilities.get(objectIndex);
        } while (currentProbability < randomDouble);
        return values.get(objectIndex);
    }

    /**
     * Gets the list of added sequences.
     * 
     * @return This matrix's list of sequences.
     */
    public List<Vector<E>> getSequenceList() {
        return sequences;
    }

    /**
     * Gets the list of values.
     * 
     * @return This matrix's list of values.
     */
    public List<T> getValueList() {
        return values;
    }

    /**
     * Gets the matrix of probabilities. First list's indexes represent
     * sequences, second list represent values.
     * 
     * @return This matrix's probabilities.
     */
    public List<List<Double>> getProbabilities() {
        if (probabilities == null) {
            initProbabilies();
        }
        return probabilities;
    }
}
