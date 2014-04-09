package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.SortedSet;
import java.util.TreeSet;

import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

/**This rater favors songs whose highest and lowest melody pitch values
 * are equal or close*/
public class MelodyPitchRangeRater extends SubRater{
    
    /**Global variable to hold unique pitch values*/
    private SortedSet<Integer> pitchValues;
    
    public MelodyPitchRangeRater(double weight){
        super.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        /**Set global variable*/
        pitchValues = new TreeSet<Integer>();
        
        /**Iterate through every melody track to check pitch values*/
        for(Track track : song.getTaggedTracks(TrackTag.MELODY)){
            checkPart(track.getPart());
        }
  
        /**If unique pitch values are empty, return rating 0*/
        if(pitchValues.isEmpty())
            return 0;
        
        /**Retrieve highest and lowest pitch values and save them as double*/
        double lowest = pitchValues.first();
        double highest = pitchValues.last();
        
        /**If lowest pitch value is 0, increase both lowest and highest with 1,
         * to be able to calculate difference otherwise just calculate difference then return*/
        return (lowest == 0) ? ++lowest/++highest : lowest/highest;
    }

    private void checkPart(Part part){
        /**Iterate through every phrase and retrive 
         * highest and lowest pitch value and save to global variable
         * */
        for(Phrase phrase : part.getPhraseArray()){
            pitchValues.add(phrase.getHighestPitch());
            pitchValues.add(phrase.getLowestPitch());
        }
    }
    
}
