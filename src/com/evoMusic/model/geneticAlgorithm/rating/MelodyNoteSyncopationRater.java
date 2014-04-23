package com.evoMusic.model.geneticAlgorithm.rating;

import java.math.BigDecimal;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

/**Rater that favors song whose melody track has a lot of notes
 * who sustain across the beats*/
public class MelodyNoteSyncopationRater extends SubRater{

    public MelodyNoteSyncopationRater(double weight){
        super.setWeight(weight);
    }
    
    @Override
    public double rate(Song song){
        double partRating = 0;
        double nbrOfTracks = 0;
        /**Iterate through every melody track*/
        for(Track track : song.getTaggedTracks(TrackTag.MELODY)){
            partRating += ratePart(track.getPart());
            nbrOfTracks++;
        }
        
        /**If nbr of melody track and part rating is larger than 0 
         * return partRating divided by nbr of melody tracks
         * */
        return (nbrOfTracks == 0 || partRating == 0) ? 0
                                                     : partRating / nbrOfTracks;
    }
    
    
    private double ratePart(Part part){
        double noteCount = 0;
        double sustainCount = 0;
        
        /**Iterate through every phrase in part*/
        for(Phrase phrase : part.getPhraseArray()){
            /**Variable to keep track of where in the beat*/
            double beatCount = 0;
            /**Iterate through every note in phrase*/
            for(Note note : phrase.getNoteArray()){
                /**If note is a rest, skip to next*/
                if(note.getPitch() == Note.REST)
                    continue;
                /**increase note and beat count*/
                noteCount++;
                beatCount += note.getRhythmValue();
                /**If duration is larger than 1, then note is going to sustain over beat 
                 * and we increase sustain count and skip to next*/
                double noteDuration = note.getDuration();
                if(noteDuration >= 1){
                    sustainCount++;
                    continue;
                }
                
                /**Calculate amount left of beat, if duration is larger then amount left on beat
                 * it is going to sustain over that beat and we increase sustain count
                 * */
                
                /***Best way to get fractional part of double!? convert to string and use split, WORKS, BUT UNEFFICENT? 
                 * MODOLO 1, MAY GET A CLOSE REPRESENTATION BUT NOT EXACT FOR ALL DOUBLE VALUES
                 * BIG DECIMAL GETS EXACT REPRESENTATION BUT GOES TRHOUGH STRING AS WELL
                 * BELOW IS DIFFERENT WAYS TO CALCULATE LEFT ON BEAT BY EXTRACTING FRACTIONAL PART. 
                 * USING BIGDECIMAL FOR NOW
                 */
                //double leftOnBeat = 1.0 - Double.parseDouble("0."+String.valueOf(beatCount).split("\\.")[1]);
                //double leftOnBeat = 1.0 - Math.abs(beatCount - (int) beatCount);
                //double leftOnBeat = 1.0 - beatCount % 1;
                //double leftOnBeat = 1.0 - (beatCount - Math.floor(beatCount));
                double leftOnBeat = 1.0 - BigDecimal.valueOf(beatCount).remainder(BigDecimal.ONE).doubleValue();                                          
                
                if(leftOnBeat < noteDuration)
                    sustainCount++;
            }
        }
        
        /**If note and sustain count is larger than 0 return sustain count divided by note count*/
        return (noteCount == 0 || sustainCount == 0) ? 0
                                                     : sustainCount / noteCount;
    }
}
