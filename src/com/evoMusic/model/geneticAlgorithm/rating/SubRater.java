package com.evoMusic.model.geneticAlgorithm.rating;

import com.evoMusic.model.Song;

public abstract class SubRater {
    private double weight;
    
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
    public double getWeight(){
        return weight;
    }
    
    /**
     * Set the weight of this rater, value must be between 0 and 1
     */
    public void setWeight(double weight){
        if (weight >= 0 && weight <= 1){
            this.weight = weight;
        } else {
            System.err.println("Weight must be between 0 and 1");
        }
    }
}
