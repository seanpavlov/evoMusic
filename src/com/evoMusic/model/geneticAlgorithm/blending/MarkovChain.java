package com.evoMusic.model.geneticAlgorithm.blending;

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
import com.evoMusic.util.TrackTag;

public class MarkovChain {

    private int numberOfLookbacks;

    private Random rand;
    private List<IntervalSong> intervalledSongs;
    private List<MarkovTrack> markovTracks;
    private int numberOfTracks;

    public MarkovChain(int lookbacks, List<Song> songs) {
        if (lookbacks < 0) {
            throw new IllegalArgumentException("Negative lookback value");
        }
        this.numberOfLookbacks = lookbacks;
        this.rand = new Random();
        this.intervalledSongs = new ArrayList<IntervalSong>();
        List<Song> trimmedSongs = trimSongParts(songs);
        for (Song currentSong : trimmedSongs) {
            intervalledSongs.add(new IntervalSong(currentSong));
        }
        numberOfTracks = intervalledSongs.get(0).getTracks().size();
        markovTracks = new ArrayList<MarkovTrack>(numberOfTracks);
        initProbabilityMatrices();
    }

    // TODO Implement and make sure all tracks match each other.
    private List<Song> trimSongParts(List<Song> songList) {
        Set<TrackTag> allTrackTags = new HashSet<TrackTag>();
        List<Song> trimmedSongs = new ArrayList<Song>();
        Iterator<TrackTag> tagIterator;
        TrackTag currentTag;
        // Finding all track tags.
        for (Song song : songList) {
            for (Track track : song.getTracks()) {
                tagIterator = track.getTags().iterator();
                while (tagIterator.hasNext()) {
                    currentTag = tagIterator.next();
                    if (currentTag != TrackTag.NONE) {
                        allTrackTags.add(currentTag);
                    }
                }
            }
        }

        // Adding empty songs.
        for (int i = 0; i < songList.size(); i++) {
            trimmedSongs.add(new Song(new Score()));
        }

        tagIterator = allTrackTags.iterator();
        while (tagIterator.hasNext()) {
            currentTag = tagIterator.next();
            for (int songIndex = 0; songIndex < songList.size(); songIndex++) {
                Song song = songList.get(songIndex);
                boolean foundTrack = false;
                for (Track track : song.getTracks()) {
                    if (track.getTags().contains(currentTag)) {
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

    public Song generateNew() {
        double longestDuration = 0;
        double currentDuration;
        for (IntervalSong currentIntervalSong : intervalledSongs) {
            for (IntervalTrack track : currentIntervalSong.getTracks()) {
                currentDuration = 0;
                for (double duration : track.getDurations()) {
                    currentDuration += duration;
                }
                if (currentDuration > longestDuration) {
                    longestDuration = currentDuration;
                }
            }
        }
        return generateNew(longestDuration);
    }

    public Song generateNew(double maxSongDuration) {

        IntervalSong newSong = new IntervalSong(getRandomTempo());

        // For each track.
        int numberOfSongs = intervalledSongs.size();
        IntervalTrack randomTrack;
        for (int trackIndex = 0; trackIndex < numberOfTracks; trackIndex++) {
            randomTrack = intervalledSongs.get(
                    (int) (rand.nextDouble() * numberOfSongs)).getTrack(
                    trackIndex);
            newSong.addTrack(markovTracks.get(trackIndex).generateNew(
                    maxSongDuration, randomTrack.getInstrument(),
                    randomTrack.getChannel(), randomTrack.getFirstNote()));
        }
        return newSong.toSong();
    }

    // TODO Make more random.
    private double getRandomTempo() {
        double tempo = 0;
        for (IntervalSong song : intervalledSongs) {
            tempo += song.getTempo();
        }
        return tempo / intervalledSongs.size();
    }

    private void initProbabilityMatrices() {
        int[] currentIntervals;
        double[] currentRythmValues;
        double[] currentDurations;
        IntervalTrack currentIntervalTrack;
        MarkovTrack currentMarkovTrack;

        Vector<Integer> sequence;
        int currentIntervalsLength;

        for (int i = 0; i < numberOfTracks; i++) {
            markovTracks.add(new MarkovTrack(numberOfLookbacks));
        }

        for (IntervalSong currentIntervalSong : intervalledSongs) {
            // for each part
            for (int partIndex = 0; partIndex < numberOfTracks; partIndex++) {
                currentIntervalTrack = currentIntervalSong.getTrack(partIndex);
                currentMarkovTrack = markovTracks.get(partIndex);
                currentIntervals = currentIntervalTrack.getIntervals();
                currentRythmValues = currentIntervalTrack.getRythmValues();
                currentDurations = currentIntervalTrack.getDurations();

                currentIntervalsLength = currentIntervals.length;

                for (int i = 0; i < currentIntervalsLength
                        - (numberOfLookbacks); i++) {
                    // Adding for empty sequences.
                    sequence = new Vector<Integer>();
                    currentMarkovTrack.addCountInterval(sequence,
                            currentIntervals[i]);
                    currentMarkovTrack.addCountToRythmValue(sequence,
                            currentRythmValues[i]);
                    currentMarkovTrack.addCountToDuration(sequence,
                            currentDurations[i]);

                    // Adding for longer sequences.
                    for (int j = i; j < i + numberOfLookbacks; j++) {
                        sequence = new Vector<Integer>(sequence);
                        sequence.add(currentIntervals[j]);
                        currentMarkovTrack.addCountInterval(sequence,
                                currentIntervals[j + 1]);
                        currentMarkovTrack.addCountToRythmValue(sequence,
                                currentRythmValues[j + 1]);
                        currentMarkovTrack.addCountToDuration(sequence,
                                currentDurations[j + 1]);
                    }

                }
                sequence = new Vector<Integer>();
                for (int i = numberOfLookbacks; i > 0; i--) {
                    sequence.add(currentIntervalsLength - i);
                }
                currentMarkovTrack.addCountToRythmValue(sequence,
                        currentRythmValues[currentRythmValues.length - 1]);
                currentMarkovTrack.addCountToDuration(sequence,
                        currentDurations[currentDurations.length - 1]);
            }
        }
        for (MarkovTrack track : markovTracks) {
            track.initProbabilities();
        }

    }
}
