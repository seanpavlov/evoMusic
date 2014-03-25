package com.evoMusic.model.geneticAlgorithm.rating;

import jm.music.data.Part;
import jm.music.tools.ChordAnalysis;

import com.evoMusic.model.Song;
import com.evoMusic.util.TrackTag;

public class ChordRater extends SubRater{
    private ChordAnalysis ca = new ChordAnalysis();
 
    @Override
    public double rate(Song song) {
        double rating = 0;
        
        for(Part part : song.getTaggedTracks(TrackTag.CHORDS)){
            rating += this.ratePart(part);
        }
        return rating;
    }
    
    private double ratePart(Part part){
        return 0;
        ChordAnalysis ca = new ChordAnalyzis();
    }

}
