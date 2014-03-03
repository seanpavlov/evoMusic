package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.Scanner;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;

public class UserRater extends SubRater {
    /**
     * Constructor, creates a user rater
     * 
     * @param weight
     */
    public UserRater(double weight){
        super.setWeight(weight);
    }
    
    @Override
    public double rate(Song song) {
        double rating = 0.0;
        Translator.INSTANCE.playSong(song);
        Scanner sc = new Scanner(System.in);
        System.out.println("Waiting for user rating input : ");
        rating = sc.nextDouble();
        return rating;
    }
}
