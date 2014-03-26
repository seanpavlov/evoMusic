package com.evoMusic.model.geneticAlgorithm.rating;

import jm.util.Play;

import com.evoMusic.controller.InputController;
import com.evoMusic.model.Song;
import com.evoMusic.util.Translator;

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
    public double rate(final Song song) {
        double rating = 0.0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Translator.INSTANCE.playSong(song);     
            }
        }).start();
        System.out.println("Waiting for user rating input : ");
        String temp = InputController.SCANNER.nextLine();
        try{
            rating = Double.parseDouble(temp);
        }catch(NumberFormatException e){
            System.err.println("Could not convert String to double in UserRater.");
        }
        Play.stopAudio();
        return rating;
    }
}
