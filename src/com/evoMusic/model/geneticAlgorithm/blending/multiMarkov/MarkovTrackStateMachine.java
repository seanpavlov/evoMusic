package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jm.music.data.Note;

import com.evoMusic.model.geneticAlgorithm.blending.IntervalTrack;
import com.evoMusic.model.geneticAlgorithm.blending.ProbabilityMatrix;
import com.evoMusic.util.TrackTag;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MarkovTrackStateMachine {
    private ProbabilityMatrix<State, State> stateMatrix;
    private int numberOfLookbacks;

    /**
     * Creates a markov chain containing a probability matrix for each property
     * in a track that.
     * 
     * @param lookbacks
     */
    public MarkovTrackStateMachine(int lookbacks) {
        this.numberOfLookbacks = lookbacks;
        this.stateMatrix = new ProbabilityMatrix<State, State>();
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
    public StateTrack generateNew(double songDuration, int instrument,
            int channel, int firstNote, TrackTag tag) {

        ArrayList<State> trackStates = new ArrayList<State>();
        double trackLength = 0;
        boolean isResting = false;
        Vector<State> currentSequence;
        State nextState;

        while (trackLength < songDuration) {
            // Building the track.
            ArrayList<State> currentStates = new ArrayList<State>();

            for (int stateIndex = 0; trackLength < songDuration; stateIndex++) {
                currentSequence = getNextSequence(stateIndex,
                        currentStates);
                nextState = stateMatrix.getNext(currentSequence);
                if (nextState == null) {
                    break; // Has come to the end of the song, must start
                           // over.
                }
                // Make sure the first interval isn't a restback (high
                // positive number)
                if (nextState.getHighestInterval() < -127 && trackLength == 0) {
                    isResting = true;
                } else {
                    // Make sure no dubbel rest/restback is added.
                    while (true) {
                        if (Math.abs(nextState.getHighestInterval()) < 127) {
                            break;
                        }
                        if (isResting && nextState.getHighestInterval() > 127) {
                            isResting = false;
                            break;
                        }
                        if ((!isResting) && nextState.getHighestInterval() < -127) {
                            isResting = true;
                            break;
                        }
                        nextState = stateMatrix.getNext(currentSequence);
                    }
                }
                trackLength += nextState.getRhythmValue();
                currentStates.add(nextState);
            }
            trackStates.addAll(currentStates);
        }

        // Adding the last rythmValues and durations.
        
        double durationLeft = songDuration - trackLength;
        State lastState = new State(songDuration);
        lastState.addNote(new Note(Note.REST, durationLeft));
        trackStates.add(lastState);
        
//        currentSequence = new Vector<State>();
//        for (int seqIndex = numberOfLookbacks; seqIndex > 0; seqIndex--) {
//            currentSequence.add(trackStates.get(trackStates.size()
//                    - seqIndex));
//        }
////        nextRhythmValue = rhythmValueMatrix.getNext(currentSequence);
////        nextDuration = durationMatrix.getNext(currentSequence);
//        nextDynamic = dynamicMatrix.getNext(currentSequence);
////        trackLength += nextRhythmValue;
//        trackRhythmValues.add(nextRhythmValue);
//        trackDurations.add(nextDuration);
//        trackDynamics.add(nextDynamic);

        StateTrack iTrack = new StateTrack(firstNote, instrument,
                channel, trackStates, tag);
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
    public void addCountState(Vector<State> sequence, State value) {
        stateMatrix.addCount(sequence, value);
    }

    /**
     * Initiates the probability matrices in this markov track. Call this method
     * sparingly, since it is relatively recource heavy.
     */
    public void initProbabilities() {
        stateMatrix.initProbabilies();
    }

    /**
     * Gets the next sequence in the given sequence that is prior to the given
     * index
     * 
     * @param intervalIndex The index of the value that comes after the requested sequence.
     * @param currentStates  The list of intervals that the sequence will be picked from.
     * @return
     */
    private Vector<State> getNextSequence(int intervalIndex,
            List<State> currentStates) {
        // TODO possible to optimize by fetching a previously cached sequence.
        int lookback;
        // Adding to sequence, wont add more than is available.
        Vector<State> currentSequence = new Vector<State>();
        for (int seqIndex = 0; seqIndex < numberOfLookbacks; seqIndex++) {
            lookback = intervalIndex - (numberOfLookbacks - seqIndex);
            if (lookback >= 0) {
                currentSequence.add(currentStates.get(lookback));

            }
        }
        return currentSequence;
    }
}
