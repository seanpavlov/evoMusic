package com.evoMusic.model.geneticAlgorithm.rating;

import com.evoMusic.model.Song;

/**
 * Uses ScaleWhizz to rate just a segment of a song. For songs 
 * where tone are out of scale but far from each other 
 */
public class SegmentScaleRater extends SubRater {

    private final double minWindowSize = 8.0;
    private double maxWindowSize;
    private final ScaleWhizz scaleRater;
    private Song song;
    private int rounds;
    
    public SegmentScaleRater(double weight) {
        this.scaleRater = new ScaleWhizz(weight);
    }
    
    /**
     * Rates segments of a given windowSize. Recurses until minimum window size
     * is reached.
     * @param currentStartTime Where the start of the segment is in he song
     * @param windowSize how big the segment will be
     * @return the sum of rating for every segment
     */
    private double rateSegments(double currentStartTime, double windowSize) {
        rounds++;
        // Are we at end of the song?
        if (currentStartTime + windowSize >= song.getScore().getEndTime()) {
            // Is this the minimum window size?
            if (windowSize <= minWindowSize) {
                return scaleRater.rateSegment(song, currentStartTime, 
                        currentStartTime + windowSize);
            } else {
                // Decrease window size and start over
                return scaleRater.rateSegment(song, currentStartTime, 
                        currentStartTime + windowSize)
                            + rateSegments(0, Math.max(windowSize / 2, minWindowSize));
            }
        }
        
        double nextStartTime = currentStartTime + windowSize / 2;
//        System.out.println("from "+currentStartTime 
//                + "\t\tto " + (currentStartTime + windowSize)
//                + "\t\trate: " + scaleRater.rateSegment(song, currentStartTime, 
//                currentStartTime + windowSize));
        return scaleRater.rateSegment(song, currentStartTime, 
                currentStartTime + windowSize) + 
                rateSegments(nextStartTime, windowSize);
    }
    
    @Override
    public double rate(Song song) {
        this.rounds = 0;
        this.song = song;
        this.maxWindowSize = Math.max(song.getScore().getEndTime()/8, minWindowSize);
        
        double res = rateSegments(0, maxWindowSize);
        return Math.pow(res / rounds, 5);
    }
}
