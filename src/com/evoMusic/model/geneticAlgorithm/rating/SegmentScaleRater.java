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
    private double k;
    private double stepsForThisWindow;
    
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
        // Are we at end of the song?
        if (currentStartTime + windowSize >= song.getScore().getEndTime()) {
            k = maxWindowSize / windowSize;
            stepsForThisWindow = Math.ceil((song.getScore().getEndTime() / ((windowSize) / 2)) - 1);
            // Is this the minimum window size?
            if (windowSize <= minWindowSize) {
                return rateSegment(song, currentStartTime, 
                        currentStartTime + windowSize);
            } else {
                // Decrease window size and start over
                return rateSegment(song, currentStartTime, 
                        currentStartTime + windowSize)
                            + rateSegments(0, Math.max(windowSize / 2, minWindowSize));
            }
        }
        
        double nextStartTime = currentStartTime + windowSize / 2;
        return rateSegment(song, currentStartTime, 
                currentStartTime + windowSize) + 
                rateSegments(nextStartTime, windowSize);
    }
    
    private double rateSegment(Song segment, double from, double to) {
        return k * scaleRater.rateSegment(segment, from, to) / stepsForThisWindow;
    }
    
    @Override
    public double rate(Song song) {
        this.song = song;
        this.maxWindowSize = Math.max(song.getScore().getEndTime()/8, minWindowSize);
        double windowSize = maxWindowSize;
        double sumK = maxWindowSize/windowSize;
        k = 1;
        stepsForThisWindow = Math.ceil((song.getScore().getEndTime() / (maxWindowSize /  2)) - 1);
        double res = rateSegments(0, maxWindowSize);
        
        while (windowSize > minWindowSize) {
            windowSize = Math.max(windowSize/2, minWindowSize);
            sumK += maxWindowSize/windowSize;
        }
        if (res / sumK > 1.1 || res / sumK < -0.1) {
            System.err.println("WARNING: SegemntScaleRater"
                    + " got a invalid rating, "
                    + "correcting it...");
        }
        return Math.max(Math.min(res / sumK, 1), 0);
    }
}
