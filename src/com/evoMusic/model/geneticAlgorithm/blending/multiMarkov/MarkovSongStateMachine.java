package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
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
import com.evoMusic.util.TrackTag;

public class MarkovSongStateMachine {
    private int numberOfLookbacks;

    private Random rand;
    private List<StateSong> statedSongs;
    private List<MarkovTrackStateMachine> markovTracks;
    private int numberOfTracks;

    /**
     * Creates a new markov chain with the given songs. The larger the lookback
     * value is, the more like the originals a new generated song will be and
     * thus have better quality. As such, one might want to set this value as
     * high as possible. One problem is that the less the given songs are alike
     * each other, the smaller the lookback value must be in order for the songs
     * to be able to merge together at all. It is not trivial to find the most
     * optimal value, but it should not be too difficult to find by trying
     * different values.
     * 
     * @param lookbacks
     *            The value that controls how alike the generated songs are to
     *            the originals and how well the songs merge with each other.
     *            Larger lookback equals better quality, but lower merge points.
     * @param songs
     *            The given songs that will be merged when generateNew is
     *            called. This list may contain only one song.
     */
    public MarkovSongStateMachine(int lookbacks, List<Song> songs) {
        if (lookbacks < 0) {
            throw new IllegalArgumentException("Negative lookback value");
        }
        this.numberOfLookbacks = lookbacks;
        this.rand = new Random();
        this.statedSongs = new ArrayList<StateSong>();
        List<Song> trimmedSongs = trimSongParts(songs);
        for (Song currentSong : trimmedSongs) {
            statedSongs.add(new StateSong(currentSong));
        }
        numberOfTracks = statedSongs.get(0).getTracks().size();
        markovTracks = new ArrayList<MarkovTrackStateMachine>(numberOfTracks);
        initProbabilityMatrices();
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

    /**
     * Generates a new song that is statistically alike to the songs given in
     * the constructor of this object. This method can be called several times
     * on the same instance and may return different songs each call. The
     * duration of the generated song will be the same as the longest song given
     * in this objects constructor.
     * 
     * @return A new song generated by merging the songs given in this objects
     *         constructor.
     */
    public Song generateNew() {
        double longestDuration = 0;
        double currentDuration;
        for (StateSong currentStateSong : statedSongs) {
            for (StateTrack track : currentStateSong.getTracks()) {
                currentDuration = 0;
                for (State state : track.getStates()) {
                    currentDuration += state.getRhythmValue();
                }
                if (currentDuration > longestDuration) {
                    longestDuration = currentDuration;
                }
            }
        }
        return generateNew(longestDuration);
    }

    /**
     * Generates a new song that is statistically alike to the songs given in
     * the constructor of this object. This method can be called several times
     * on the same instance and may return different songs each call.
     * 
     * @param songDuration
     *            The duration that the generated song will have.
     * @return A new song generated by merging the songs given in this objects
     *         constructor.
     */
    public Song generateNew(double songDuration) {

        StateSong newSong = new StateSong(getRandomTempo());

        // For each track.
        int numberOfSongs = statedSongs.size();
        StateTrack randomTrack = null;
        boolean chosenTrackIsEmpty;
        for (int trackIndex = 0; trackIndex < numberOfTracks; trackIndex++) {
            // make sure an empty track doesn't give their instruments etc.
            chosenTrackIsEmpty = true;
            while (chosenTrackIsEmpty) {
                randomTrack = statedSongs.get(
                        (int) (rand.nextDouble() * numberOfSongs)).getTrack(
                        trackIndex);
                chosenTrackIsEmpty = randomTrack.getStates().size() == 0;
            }
            
            newSong.addTrack(markovTracks.get(trackIndex).generateNew(
                    songDuration, randomTrack.getInstrument(),
                    randomTrack.getChannel(), randomTrack.getFirstNote(), randomTrack.getTag()));
        }
        return newSong.toSong();
    }

    /**
     * Gets a tempo value somewhere between the largest and the smallest tempo
     * of the songs given in this objects constructor.
     * 
     * @return A tempo between the tempo values of this instance's songs.
     */
    private double getRandomTempo() {
        // TODO Make more random.
        double tempo = 0;
        for (StateSong song : statedSongs) {
            tempo += song.getTempo();
        }
        return tempo / statedSongs.size();
    }

    /**
     * Initiates the probability matrices for each song property in each track.
     * This is probably the most resource heavy method in this class.
     */
    private void initProbabilityMatrices() {
        List<State> currentStates;
        double[] currentRythmValues;
        double[] currentDurations;
        int[] currentDynamics;
        StateTrack currentStateTrack;
        MarkovTrackStateMachine currentMarkovTrack;

        Vector<State> sequence;
        int currentNumberOfStates;

        for (int i = 0; i < numberOfTracks; i++) {
            markovTracks.add(new MarkovTrackStateMachine(numberOfLookbacks));
        }

        for (StateSong currentStateSong : statedSongs) {
            // for each part
            for (int partIndex = 0; partIndex < numberOfTracks; partIndex++) {
                currentStateTrack = currentStateSong.getTrack(partIndex);
                currentMarkovTrack = markovTracks.get(partIndex);
                currentStates = currentStateTrack.getStates();
                if(currentStates.size() == 0) {
                    continue;
                }
//                currentRythmValues = currentStateTrack.getRythmValues();
//                currentDurations = currentStateTrack.getDurations();
//                currentDynamics = currentStateTrack.getDynamics();

                currentNumberOfStates = currentStates.size();

                for (int i = 0; i < currentNumberOfStates
                        - (numberOfLookbacks); i++) {
                    // Adding for empty sequences.
                    sequence = new Vector<State>();
                    currentMarkovTrack.addCountState(sequence, currentStates.get(i));
//                    currentMarkovTrack.addCountInterval(sequence,
//                            currentStates[i]);
//                    currentMarkovTrack.addCountToRhythmValue(sequence,
//                            currentRythmValues[i]);
//                    currentMarkovTrack.addCountToDuration(sequence,
//                            currentDurations[i]);
//                    currentMarkovTrack.addCountToDynamic(sequence, currentDynamics[i]);
                    

                    // Adding for longer sequences.
                    for (int j = i; j < i + numberOfLookbacks; j++) {
                        sequence = new Vector<State>(sequence);
                        sequence.add(currentStates.get(j));
                        currentMarkovTrack.addCountState(sequence, currentStates.get(j + 1));
//                        currentMarkovTrack.addCountInterval(sequence,
//                                currentStates[j + 1]);
//                        currentMarkovTrack.addCountToRhythmValue(sequence,
//                                currentRythmValues[j + 1]);
//                        currentMarkovTrack.addCountToDuration(sequence,
//                                currentDurations[j + 1]);
//                        currentMarkovTrack.addCountToDynamic(sequence, currentDynamics[j + 1]);
                    }

                }
//                sequence = new Vector<Integer>();
//                for (int i = numberOfLookbacks; i > 0; i--) {
//                    sequence.add(currentStates[currentNumberOfStates - i]);
//                }
//                currentMarkovTrack.addCountToRhythmValue(sequence,
//                        currentRythmValues[currentRythmValues.length - 1]);
//                currentMarkovTrack.addCountToDuration(sequence,
//                        currentDurations[currentDurations.length - 1]);
//                currentMarkovTrack.addCountToDynamic(sequence, currentDynamics[currentDynamics.length - 1]);
            }
        }
        for (MarkovTrackStateMachine track : markovTracks) {
            track.initProbabilities();
        }

    }
}
