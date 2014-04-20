package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.evoMusic.util.TrackTag;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MarkovTrack {

    private ProbabilityMatrix<Integer, Integer> intervalMatrix;
    private ProbabilityMatrix<Integer, Double> rhythmValueMatrix;
    private ProbabilityMatrix<Integer, Double> durationMatrix;
    private ProbabilityMatrix<Integer, Integer> dynamicMatrix;
    private int numberOfLookbacks;

    /**
     * Creates a markov chain containing a probability matrix for each property
     * in a track that.
     * 
     * @param lookbacks
     */
    public MarkovTrack(int lookbacks) {
        this.numberOfLookbacks = lookbacks;
        this.intervalMatrix = new ProbabilityMatrix<Integer, Integer>();
        this.rhythmValueMatrix = new ProbabilityMatrix<Integer, Double>();
        this.durationMatrix = new ProbabilityMatrix<Integer, Double>();
        this.dynamicMatrix = new ProbabilityMatrix<Integer, Integer>();
    }

    /**
     * Generates an interval track from the probabilities in this markov chain.
     * 
     * @param songDuration
     *            The length of the track that will be generated.
     * @param instrument
     *            The instrument that the generated track should have.
     * @param channel
     *            The channel that the generated track should have.
     * @param firstNote
     *            The first note of the track that will be generated.
     * @return An interval track generated from the probabilities in this markov
     *         chain.
     */
    public IntervalTrack generateNew(double songDuration, int instrument,
            int channel, int firstNote, TrackTag tag) {

        ArrayList<Integer> trackIntervals = new ArrayList<Integer>();
        ArrayList<Double> trackRhythmValues = new ArrayList<Double>();
        ArrayList<Double> trackDurations = new ArrayList<Double>();
        ArrayList<Integer> trackDynamics = new ArrayList<Integer>();
        double trackLength = 0;
        boolean isResting = false;
        Vector<Integer> currentSequence;
        Integer nextInterval;
        Double nextRhythmValue;
        Double nextDuration;
        Integer nextDynamic;

        while (trackLength < songDuration) {
            // Building the track.
            ArrayList<Integer> currentIntervals = new ArrayList<Integer>();
            ArrayList<Double> currentRhythmValues = new ArrayList<Double>();
            ArrayList<Double> currentDurations = new ArrayList<Double>();
            ArrayList<Integer> currentDynamics = new ArrayList<Integer>();

            for (int intervalIndex = 0; trackLength < songDuration; intervalIndex++) {
                currentSequence = getNextSequence(intervalIndex,
                        currentIntervals);
                nextInterval = intervalMatrix.getNext(currentSequence);
                if (nextInterval == null) {
                    break; // Has come to the end of the song, must start
                           // over.
                }
                // Make sure the first interval isn't a restback (high
                // positive number)
                if (nextInterval < -127 && trackLength == 0) {
                    isResting = true;
                } else {
                    // Make sure no dubbel rest/restback is added.
                    while (true) {
                        if (Math.abs(nextInterval) < 127) {
                            break;
                        }
                        if (isResting && nextInterval > 127) {
                            isResting = false;
                            break;
                        }
                        if ((!isResting) && nextInterval < -127) {
                            isResting = true;
                            break;
                        }
                        nextInterval = intervalMatrix.getNext(currentSequence);
                    }
                }

                nextRhythmValue = rhythmValueMatrix.getNext(currentSequence);
                nextDuration = durationMatrix.getNext(currentSequence);
                nextDynamic = dynamicMatrix.getNext(currentSequence);
                trackLength += nextRhythmValue;
                currentIntervals.add(nextInterval);
                currentRhythmValues.add(nextRhythmValue);
                currentDurations.add(nextDuration);
                currentDynamics.add(nextDynamic);
            }
            trackIntervals.addAll(currentIntervals);
            trackRhythmValues.addAll(currentRhythmValues);
            trackDurations.addAll(currentDurations);
            trackDynamics.addAll(currentDynamics);
        }

        // Adding the last rythmValues and durations.
        currentSequence = new Vector<Integer>();
        for (int seqIndex = numberOfLookbacks; seqIndex > 0; seqIndex--) {
            currentSequence.add(trackIntervals.get(trackIntervals.size()
                    - seqIndex));
        }
        nextRhythmValue = rhythmValueMatrix.getNext(currentSequence);
        nextDuration = durationMatrix.getNext(currentSequence);
        nextDynamic = dynamicMatrix.getNext(currentSequence);
        trackLength += nextRhythmValue;
        trackRhythmValues.add(nextRhythmValue);
        trackDurations.add(nextDuration);
        trackDynamics.add(nextDynamic);

        IntervalTrack iTrack = new IntervalTrack(firstNote, instrument,
                channel, Ints.toArray(trackIntervals),
                Doubles.toArray(trackRhythmValues),
                Doubles.toArray(trackDurations), Ints.toArray(trackDynamics), tag);
        return iTrack;
    }

    /**
     * Adds a count to the interval property in this markov track.
     * 
     * @param sequence
     *            The sequence that comes prior to the given interval.
     * @param value
     *            The interval which count will be incremented.
     */
    public void addCountInterval(Vector<Integer> sequence, Integer value) {
        intervalMatrix.addCount(sequence, value);
    }

    /**
     * Adds a count to the rhythm value property in this markov track.
     * 
     * @param sequence
     *            The sequence that comes prior to the given rhythm value.
     * @param value
     *            The rhythm value which count will be incremented.
     */
    public void addCountToRhythmValue(Vector<Integer> sequence, Double value) {
        rhythmValueMatrix.addCount(sequence, value);
    }

    /**
     * Adds a count to the duration property in this markov track.
     * 
     * @param sequence
     *            The sequence that comes prior to the given duration.
     * @param value
     *            The duration which count will be incremented.
     */
    public void addCountToDuration(Vector<Integer> sequence, Double value) {
        durationMatrix.addCount(sequence, value);
    }
    
    /**
     * Adds a count to the dynamic property in this markov track.
     * 
     * @param sequence
     *            The sequence that comes prior to the given dynamic.
     * @param value
     *            The dynamic which count will be incremented.
     */
    public void addCountToDynamic(Vector<Integer> sequence, Integer value) {
        dynamicMatrix.addCount(sequence, value);
    }

    /**
     * Initiates the probability matrices in this markov track. Call this method
     * sparingly, since it is relatively recource heavy.
     */
    public void initProbabilities() {
        intervalMatrix.initProbabilies();
        rhythmValueMatrix.initProbabilies();
        durationMatrix.initProbabilies();
    }

    /**
     * Gets the next sequence in the given sequence that is prior to the given
     * index
     * 
     * @param intervalIndex The index of the value that comes after the requested sequence.
     * @param currentIntervals  The list of intervals that the sequence will be picked from.
     * @return
     */
    private Vector<Integer> getNextSequence(int intervalIndex,
            List<Integer> currentIntervals) {
        // TODO possible to optimize by fetching a previously cached sequence.
        int lookback;
        // Adding to sequence, wont add more than is available.
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
