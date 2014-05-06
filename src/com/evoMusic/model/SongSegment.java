package com.evoMusic.model;


/**
 * A song with a start and end time that does not have to be 
 * 0.0 and score.getEndTime();
 */
public class SongSegment extends Song {
    
    private final double startTime, endTime;
    
    public SongSegment(Song segment, double startTime, double endTime) {
        super(segment.copy(startTime, endTime));
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

    public double getLength() {
        return getScore().getEndTime();
    }
    
    public double getStartTime() {
        return startTime;
    }
    
    public double getEndTime() {
        return endTime;
    }
    

}
