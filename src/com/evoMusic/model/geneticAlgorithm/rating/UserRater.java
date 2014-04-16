package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.Scanner;

import jm.util.Play;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;

public class UserRater extends SubRater {
    private Scanner sc;
    
    /**
     * Constructor, creates a user rater
     * 
     * @param weight
     */
    public UserRater(double weight, Scanner sc){
        super.setWeight(weight);
        this.sc = sc;
    }
    
    @Override
    public double rate(final Song song) {
        double rating = 0.0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Translator.INSTANCE.play(song);     
            }
        }).start();
        System.out.println("Waiting for user rating input : ");
        String temp = sc.nextLine();
        try{
            rating = Double.parseDouble(temp);
        }catch(NumberFormatException e){
            System.err.println("Could not convert String to double in UserRater.");
        }
        Play.stopAudio();
        return rating;
    }
}
