package com.evoMusic.model.geneticAlgorithm.rating;


import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;

public class BeatRater extends ISubRater{
    
    
    /*
     * TODO Implement
     * 
     * **/
    @Override
    public double rate(Song song) {
        for(Part part : song.getScore().getPartArray())
            this.ratePart(part);
        return 0;
    }
    
    /*
     * 
     * TODO Implement
     * 
     * */
    private double ratePart(Part part){
        for(Phrase phrase : part.getPhraseArray()){
        }
        
        return 0;
    }
}
