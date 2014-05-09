package com.evoMusic.model;


/**
 * A song with a start and end time that does not have to be 
 * 0.0 and score.getEndTime();
 */
public class SongSegment {
    
    private final double startTime, endTime;
    private Song segment;
    
    public SongSegment(Song segment, double startTime, double endTime) {
        this.segment   = segment;
        this.startTime = startTime;
        this.endTime   = endTime;
    }
    
    /**
     * if no start or end is given, assume defaults
     * @param segment
     */
    public SongSegment(Song segment) {
        this(segment, 0.0, segment.getScore().getEndTime());
    }
    
    public Song getSong() {
        return segment;
    }

    public double getLength() {
        return endTime - startTime;
    }
    
    public double getStartTime() {
        return startTime;
    }
    
    public double getEndTime() {
        return endTime;
    }
    

}
