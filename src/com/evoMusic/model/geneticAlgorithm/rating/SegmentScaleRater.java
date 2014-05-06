package com.evoMusic.model.geneticAlgorithm.rating;

import com.evoMusic.model.Song;
import com.evoMusic.model.SongSegment;

public class SegmentScaleRater extends SubRater {

    private final double minWindowSize = 4.0;
    private final ScaleWhizz scaleRater = new ScaleWhizz(1);
    private Song song;
    private int rounds;
    
    
    public double rateSegments(SongSegment currentSegment, double windowSize) {
        rounds++;
        // Are we at end of the song? 
        if (currentSegment.getEndTime() == song.getScore().getEndTime()) {
            // Can we decrease the window size?
            if (windowSize <= minWindowSize) {
                return scaleRater.rate(currentSegment);
            } else {
                // Decrease window size and start over;
                return scaleRater.rate(currentSegment) + rateSegments(
                        new SongSegment(song, 0, windowSize/2), windowSize/2);
            }
        }
        
        System.out.println("curr:" + windowSize);
        
        double nextStartTime = currentSegment.getStartTime()
                + windowSize / 2;
        double nextEndTime   = currentSegment.getEndTime() 
                + windowSize / 2;
        
        if (nextEndTime > song.getScore().getEndTime()) { 
            nextEndTime = song.getScore().getEndTime();
        }
        
        final SongSegment nextSegment = 
                new SongSegment(song, nextStartTime, nextEndTime);
        
        return scaleRater.rate(currentSegment) + 
                rateSegments(nextSegment, windowSize);
    }
    
    @Override
    public double rate(Song song) {
        this.rounds = 0;
        this.song = song;
        double res = rateSegments(new SongSegment(song), 
                song.getScore().getEndTime());
        System.out.println(rounds);
        return res;
    }

}
