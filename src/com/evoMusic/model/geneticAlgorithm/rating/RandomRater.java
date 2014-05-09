package com.evoMusic.model.geneticAlgorithm.rating;

import com.evoMusic.model.Song;

public class RandomRater extends SubRater{

    public RandomRater(double weight){
        super.setWeight(weight);
    }
    
    @Override
    public double rate(Song song) {
        return Math.random();
    }

}
