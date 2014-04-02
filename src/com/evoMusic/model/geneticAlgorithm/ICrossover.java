package com.evoMusic.model.geneticAlgorithm;

import java.util.List;

import com.evoMusic.model.Song;

public interface ICrossover {

    /**
     * Takes a number of parents and blends their properties. Returns a list of
     * songs with the same size as the given list of parents.
     * 
     * @return A list containing the same number of songs as the given parents.
     */
    public List<Song> makeCrossover(List<Song> parents);
    
}
