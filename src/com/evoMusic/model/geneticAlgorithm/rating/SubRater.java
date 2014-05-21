package com.evoMusic.model.geneticAlgorithm.rating;

import com.evoMusic.model.Song;

public abstract class SubRater {
    private double targetRating;
    
    /**
     * Rates a song or a part of it depending on the raters task.
     * 
     * @param song to be rated
     * @return rate value between 0 and 1
     */
    abstract public double rate(Song song);
    
    /**
     * @return the current weight of this rater
     */
    public double getTargetRating(){
        return targetRating;
    }
    
    /**
     * Set the weight of this rater, value must be between 0 and 1
     */
    public void setTargetRating(double targetRating){
        if (targetRating >= 0 && targetRating <= 1){
            this.targetRating = targetRating;
        } else {
            System.err.println("Target rating must be between 0 and 1");
        }
    }
}
