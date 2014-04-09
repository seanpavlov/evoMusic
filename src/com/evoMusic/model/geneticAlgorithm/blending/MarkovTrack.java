package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MarkovTrack {

    private ProbabilityMatrix<Integer, Integer> intervalMatrix;
    private ProbabilityMatrix<Integer, Double> rythmValueMatrix;
    private ProbabilityMatrix<Integer, Double> durationMatrix;
    private int numberOfLookbacks;
    
    public MarkovTrack(int lookbacks) {
        this.numberOfLookbacks = lookbacks;
        this.intervalMatrix = new ProbabilityMatrix<Integer, Integer>();
        this.rythmValueMatrix = new ProbabilityMatrix<Integer, Double>();
        this.durationMatrix = new ProbabilityMatrix<Integer, Double>();
    }
    
    public IntervalTrack generateNew(double maxSongDuration, int instrument, int channel, int firstNote) {
        
        ArrayList<Integer> trackIntervals = new ArrayList<Integer>();
        ArrayList<Double> trackRythmValues = new ArrayList<Double>();
        ArrayList<Double> trackDurations = new ArrayList<Double>();
        double trackLength = 0;
        boolean isResting = false;
        Vector<Integer> currentSequence;
        Integer nextInterval;
        Double nextRythmValue;
        Double nextDuration;
        
        while (trackLength < maxSongDuration) {
            // Building the track.
            ArrayList<Integer> currentIntervals = new ArrayList<Integer>();
            ArrayList<Double> currentRythmValues = new ArrayList<Double>();
            ArrayList<Double> currentDurations = new ArrayList<Double>();

            for (int intervalIndex = 0; trackLength < maxSongDuration; intervalIndex++) {
                currentSequence = getNextSequence(intervalIndex,
                        currentIntervals);
                nextInterval = intervalMatrix.getNext(currentSequence);
                if (nextInterval == null) {
                    break; // Has come to the end of the song, must start
                           // over.
                }
                // Make sure the first interval isn't a restback (high
                // positive number) or rest.
                if (intervalIndex == 0) {
                    while (nextInterval > 127 || nextInterval < 0) {
                        nextInterval = intervalMatrix
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
                        nextInterval = intervalMatrix
                                .getNext(currentSequence);
                    }
                }

                nextRythmValue = rythmValueMatrix.getNext(currentSequence);
                nextDuration = durationMatrix.getNext(currentSequence);
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
        for (int seqIndex = numberOfLookbacks; seqIndex > 0; seqIndex--) {
            currentSequence.add(trackIntervals.get(trackIntervals.size()
                    - seqIndex));
        }
        nextRythmValue = rythmValueMatrix.getNext(currentSequence);
        nextDuration = durationMatrix.getNext(currentSequence);
        trackLength += nextRythmValue;
        trackRythmValues.add(nextRythmValue);
        trackDurations.add(nextDuration);

        IntervalTrack iTrack = new IntervalTrack(firstNote,
                instrument, channel,
                Ints.toArray(trackIntervals),
                Doubles.toArray(trackRythmValues),
                Doubles.toArray(trackDurations));
        return iTrack;
    }
    
    public void addCountInterval(Vector<Integer> sequence, Integer value) {
        intervalMatrix.addCount(sequence, value);
    }
    
    public void addCountToRythmValue(Vector<Integer> sequence, Double value) {
        rythmValueMatrix.addCount(sequence, value);
    }
    
    public void addCountToDuration(Vector<Integer> sequence, Double value) {
        durationMatrix.addCount(sequence, value);
    }
    
    public void initProbabilities() {
        intervalMatrix.initProbabilies();
        rythmValueMatrix.initProbabilies();
        durationMatrix.initProbabilies();
    }
    
    private Vector<Integer> getNextSequence(int intervalIndex,
            List<Integer> currentIntervals) {
        // Adding to sequence, wont add more than is available.
        int lookback;
        Vector<Integer> currentSequence = new Vector<Integer>();
        for (int seqIndex = 0; seqIndex < numberOfLookbacks; seqIndex++) {
            lookback = intervalIndex - (numberOfLookbacks - seqIndex);
            if (lookback >= 0) {
                currentSequence.add(currentIntervals.get(lookback));

            }
        }
        return currentSequence;
    }
    
}
