package com.evoMusic.model.geneticAlgorithm.rating;


import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;

public class BeatRater extends SubRater{  
 
    
    public double rate(Song song) {
        this.ratePart(song.getScore().getPart(0));
       /* for(Part part : song.getScore().getPartArray())
            this.ratePart(part);*/
        return 0;
    }
    
    /*
     * 
     * TODO Implement
     * 
     * */
    private double ratePart(Part part){
        for(Phrase phrase : part.getPhraseArray()){
            System.out.println("beat length: " + phrase.getBeatLength());
        }
        
        return 0;
    }
}
