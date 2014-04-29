package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.SortedSet;
import java.util.TreeSet;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

/**This rater favors songs whose highest and lowest melody pitch values
 * are equal or close*/
public class MelodyPitchRangeRater extends SubRater{
    
    public MelodyPitchRangeRater(double weight){
        super.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        /**Set sorted set variable to keep unique pitch values*/
        SortedSet<Integer> pitchValues = new TreeSet<Integer>();
        
        int highestPitch, lowestPitch;
        
        /**Iterate through every melody track to check pitch values*/
        for(Track track : song.getTaggedTracks(TrackTag.MELODY)){
            for(Phrase phrase : track.getPart().getPhraseArray()){
                highestPitch = phrase.getHighestPitch();
                lowestPitch  = phrase.getLowestPitch();
                if (highestPitch != -1 )
                    pitchValues.add(highestPitch);
                if (lowestPitch != 128) 
                    pitchValues.add(lowestPitch);
            }
        }
  
        /**If unique pitch values are empty, return rating 0*/
        if(pitchValues.isEmpty())
            return 0;
        
        /**Retrieve highest and lowest pitch values and save them as double*/
        double lowest = pitchValues.first();
        double highest = pitchValues.last();
        /**Worst case is when lowest pitch is 0 and
         * highest pitch is 127 and we return rating 0*/
        if(highest - lowest == 127)
            return 0;
        
        /**If lowest pitch value is 0 but not highest, increase both lowest and highest with 1,
         * to be able to calculate difference, otherwise just calculate difference then return*/
        return (lowest == 0 && highest != 0) ? ++lowest/++highest : lowest/highest;
    }
}
