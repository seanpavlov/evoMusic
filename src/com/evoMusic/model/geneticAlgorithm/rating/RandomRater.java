package com.evoMusic.model.geneticAlgorithm.rating;

import com.evoMusic.model.Song;

public class RandomRater extends SubRater{

    public RandomRater(double targetRating){
        super.setTargetRating(targetRating);
    }
    
    @Override
    public double rate(Song song) {
        return Math.random();
    }

}
