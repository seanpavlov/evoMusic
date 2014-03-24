package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private IntervalSong intervalledSong;
    private int[] intervalSequences;
    private int[][] intervalProbabilities;

    public MarkovChain(Song song) {
        this.intervalledSong = new IntervalSong(song);
    }
    

    
    private int getRandomNote(Phrase[] phrases) {
        return 0;
    }

    public Song generateNew() {
//        int[] firstNotes = new int[1];
//        firstNotes[0] = originalSong.getScore().getPart(0).getPhrase(0).getNote(0).getPitch();
//        Song newSong = toSong(originalIntervals, originalDurations, firstNotes);
//        newSong.getScore().setTempo(originalSong.getTempo());
//        return newSong;
        
        double longestDuration = 0;
        double currentDuration;
        for(double[] durations : originalDurations) {
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
        // TODO optimize this!
        List<List<Integer>> newIntervals = new ArrayList<List<Integer>>();
        List<List<Double>> newTimeDiff = new ArrayList<List<Double>>();
        List<List<Double>> newDuration = new ArrayList<List<Double>>();

        List<Integer> intervalList;
        List<Double> timeDiffList;
        List<Double> durationList;
        
        int[] foundIntervals;
        double[] foundDurations;
        
        // For each track:
        for (int partNumber = 0; partNumber < originalIntervals.size(); partNumber++) {
            intervalList = new ArrayList<Integer>();
            timeDiffList = new ArrayList<Double>();
            durationList = new ArrayList<Double>();
            double songLength = 0;
            int randomInt;
            
            // Add first interval
            randomInt = (int)(rand.nextDouble() * intervalList.size());
            intervalList.add(originalIntervals.get(partNumber)[randomInt]);
            
            while (songLength < maxSongDuration) {
                // TODO lookup intervals and duration at same time.
                
                // Add rest of intervals
                
                
                // Add all durations
                
            }

            newIntervals.add(intervalList);
            newTimeDiff.add(timeDiffList);
            newDuration.add(durationList);
        }
        
        // Find first notes

        // TODO convert to real notes.
        return null;
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

    private static double getRoundedStartTime(Note note) {
        double startTime = note.getSampleStartTime();
        startTime = (int) (startTime * timeDiffAccuracy);
        startTime = startTime / timeDiffAccuracy;
        return startTime;
    }
    



}
