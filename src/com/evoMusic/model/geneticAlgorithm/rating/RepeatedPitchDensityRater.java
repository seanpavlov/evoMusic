package com.evoMusic.model.geneticAlgorithm.rating;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.parameters.P;
import com.evoMusic.util.TrackTag;

/**Rater that favors song whose got two of the same pitch values 
 * in a row a lot of times in melody tracks*/
public class RepeatedPitchDensityRater extends SubRater{
    
    public RepeatedPitchDensityRater(double targetRating){
        super.setTargetRating(targetRating);
        super.setInfluenceMultiplier(P.RATER_REPEATED_PITCH_DENSITY_INFLUENCE_MUL);
    }

    @Override
    public double rate(Song song) {
        double partRating = 0;
        double nbrOfParts = 0;
        
        /**Iterate through melody tracks and add to part rating*/
        for(Track track : song.getTaggedTracks(TrackTag.MELODY)){
            partRating += ratePart(track.getPart());
            nbrOfParts++;
        }
        
        /**If nbr of parts rated is zero, return rating zero*/
        if(nbrOfParts == 0)
            return 0;
        
        /**Return median of all the rated parts*/
        return partRating/nbrOfParts;
    }
    
    /**Rates part by counting the nbr of times the same
     * pitch value comes two in a row compared to total nbr of notes
     * @param Part part to be rated
     * */
    private double ratePart(Part part){
        /**Vairable to keep track of two in a row and total nbr of notes*/
        double twoInRow = 0;
        double nbrOfNotes = 0;
        
        
        for(Phrase phrase : part.getPhraseArray()){
            Note[] notes = phrase.getNoteArray();
            if(notes.length > 0)
                nbrOfNotes++;
            for(int i = 0 ; i < notes.length -1; i++){
                int current = notes[i].getPitch();
                int next = notes[i + 1].getPitch();
                if(current == Note.REST || next == Note.REST)
                    continue;
                if(current == next)
                    twoInRow++;
                nbrOfNotes++;
            }
        }
        
        /**If nbr of notes is zero, return rating zero*/
        if(nbrOfNotes == 0 || twoInRow == 0)
            return 0;
        
        /**If difference beetween total nbr of notes and two pitch values in row is less or equal to 1
         * return rating 1.0
         * */
        double difference = nbrOfNotes - twoInRow;
        if( difference <= 1)
            return 1.0;
        
        /**Return two in row divided by total nbr of notes*/
        return twoInRow / nbrOfNotes ;
    }

}
