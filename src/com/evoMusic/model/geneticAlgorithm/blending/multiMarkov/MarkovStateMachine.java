package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.evoMusic.model.geneticAlgorithm.blending.IntervalSong;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalTrack;
import com.evoMusic.model.geneticAlgorithm.blending.MarkovTrack;
import com.evoMusic.model.geneticAlgorithm.blending.ProbabilityMatrix;
import com.evoMusic.util.TrackTag;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MarkovStateMachine {

    private ProbabilityMatrix<TempState<Integer>, TempState<Integer>> intervalMatrix;
    private ProbabilityMatrix<TempState<Integer>, TempState<Double>> rhythmValueMatrix;
    private ProbabilityMatrix<TempState<Integer>, TempState<Double>> durationMatrix;
    private ProbabilityMatrix<TempState<Integer>, TempState<Integer>> dynamicMatrix;
    private ProbabilityMatrix<TempState<Integer>, TempState<Double>> timeDeltaMatrix;
    private int numberOfLookbacks;
    private Random rand;
    private ArrayList<StateSong> intervalledSongs;
    private int numberOfTracks;

    /**
     * Creates a markov chain containing a probability matrix for each property
     * in a track that.
     * 
     * @param lookbacks
     */
    public MarkovStateMachine(int lookbacks, List<Song> songs) {
        this.numberOfLookbacks = lookbacks;
        this.intervalMatrix = new ProbabilityMatrix<TempState<Integer>, TempState<Integer>>();
        this.rhythmValueMatrix = new ProbabilityMatrix<TempState<Integer>, TempState<Double>>();
        this.durationMatrix = new ProbabilityMatrix<TempState<Integer>, TempState<Double>>();
        this.dynamicMatrix = new ProbabilityMatrix<TempState<Integer>, TempState<Integer>>();
        this.timeDeltaMatrix = new ProbabilityMatrix<TempState<Integer>, TempState<Double>>();
        
        if (lookbacks < 0) {
            throw new IllegalArgumentException("Negative lookback value");
        }
        this.numberOfLookbacks = lookbacks;
        this.rand = new Random();
        this.intervalledSongs = new ArrayList<StateSong>();
        List<Song> trimmedSongs = trimSongParts(songs);
        for (Song currentSong : trimmedSongs) {
            intervalledSongs.add(new StateSong(currentSong));
        }
        numberOfTracks = intervalledSongs.get(0).numberOfTracks();
//        markovTracks = new ArrayList<MarkovTrack>(numberOfTracks);
        initProbabilityMatrices();
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

//        ArrayList<Integer> trackIntervals = new ArrayList<Integer>();
//        ArrayList<Double> trackRhythmValues = new ArrayList<Double>();
//        ArrayList<Double> trackDurations = new ArrayList<Double>();
//        ArrayList<Integer> trackDynamics = new ArrayList<Integer>();
        
        List<TempState<Integer>> songIntervals = new ArrayList<TempState<Integer>>();
        List<TempState<Double>> songRhythmValues = new ArrayList<TempState<Double>>();
        List<TempState<Double>> songDurations = new ArrayList<TempState<Double>>();
        List<TempState<Integer>> songDynamics = new ArrayList<TempState<Integer>>();
        List<TempState<Double>> songTimeDeltas = new ArrayList<TempState<Double>>();
        
        double currentLength = 0;
        boolean[] isResting = new boolean[numberOfTracks];
        Arrays.fill(isResting, false);
        Vector<TempState<Integer>> currentSequence;
        Integer nextInterval;
        Double nextRhythmValue;
        Double nextDuration;
        Integer nextDynamic;
        Double nextTimeDelta;

        while (currentLength < songDuration) {
            // Building the track.
            List<Integer> currentIntervals = new ArrayList<Integer>();
            List<Double> currentRhythmValues = new ArrayList<Double>();
            List<Double> currentDurations = new ArrayList<Double>();
            List<Integer> currentDynamics = new ArrayList<Integer>();

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

        double durationLeft = songDuration - trackLength;
        nextRhythmValue = durationLeft;
        nextDuration = durationLeft;

        currentSequence = new Vector<Integer>();
        for (int seqIndex = numberOfLookbacks; seqIndex > 0; seqIndex--) {
            currentSequence.add(trackIntervals.get(trackIntervals.size()
                    - seqIndex));
        }
        // nextRhythmValue = rhythmValueMatrix.getNext(currentSequence);
        // nextDuration = durationMatrix.getNext(currentSequence);
        nextDynamic = dynamicMatrix.getNext(currentSequence);
        // trackLength += nextRhythmValue;
        trackRhythmValues.add(nextRhythmValue);
        trackDurations.add(nextDuration);
        trackDynamics.add(nextDynamic);

        IntervalTrack iTrack = new IntervalTrack(firstNote, instrument,
                channel, Ints.toArray(trackIntervals),
                Doubles.toArray(trackRhythmValues),
                Doubles.toArray(trackDurations), Ints.toArray(trackDynamics),
                tag);
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
     * @param intervalIndex
     *            The index of the value that comes after the requested
     *            sequence.
     * @param currentIntervals
     *            The list of intervals that the sequence will be picked from.
     * @return
     */
    private Vector<TempState<Integer>> getNextSequence(int intervalIndex,
            List<TempState<Integer>> currentIntervals) {
        // TODO possible to optimize by fetching a previously cached sequence.
        int lookback;
        // Adding to sequence, wont add more than is available.
        Vector<TempState<Integer>> currentSequence = new Vector<TempState<Integer>>();
        for (int seqIndex = 0; seqIndex < numberOfLookbacks; seqIndex++) {
            lookback = intervalIndex - (numberOfLookbacks - seqIndex);
            if (lookback >= 0) {
                currentSequence.add(currentIntervals.get(lookback));

            }
        }
        return currentSequence;
    }
    
    /**
     * Trims and orders the songs in such way that it is guaranteed that all
     * songs has the same number of tracks and that all track with a certain
     * index has the same track tag. The original songs will not be modified,
     * but the new list will have references to the old tracks.
     * 
     * @param songList
     *            The song list that is to be trimmed.
     * @return A list of songs that all have identical structure.
     */
    private List<Song> trimSongParts(List<Song> songList) {
        Set<TrackTag> allTrackTags = new HashSet<TrackTag>();
        List<Song> trimmedSongs = new ArrayList<Song>();
        Iterator<TrackTag> tagIterator;
        TrackTag currentTag;
        // Finding all track tags.
        for (Song song : songList) {
            for (Track track : song.getTracks()) {
                if (!(track.getTag() == TrackTag.NONE) && track.getTag() != null) {
                    allTrackTags.add(track.getTag());
                }
            }
        }

        // Adding empty songs.
        Score newScore;
        for (Song song : songList) {
            newScore = new Score(song.getTempo());
            trimmedSongs.add(new Song(newScore));
        }

        tagIterator = allTrackTags.iterator();
        while (tagIterator.hasNext()) {
            currentTag = tagIterator.next();
            for (int songIndex = 0; songIndex < songList.size(); songIndex++) {
                Song song = songList.get(songIndex);
                boolean foundTrack = false;
                for (Track track : song.getTracks()) {
                    if (track.getTag() == currentTag) {
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

}
