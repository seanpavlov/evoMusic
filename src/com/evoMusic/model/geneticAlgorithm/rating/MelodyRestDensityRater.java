package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.SortedSet;
import java.util.TreeSet;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

/**Rater that favors song where the average rest note density in 
 * all beats in it's melody tracks are close or eqaul
 * to the least rest note density beat found in songs melody tracks*/
public class MelodyRestDensityRater extends SubRater{
    
    /**Global variables to keep nbr of rest notes in different beats,
     * total nbr of beats and total nbr of rest notes 
     * */
    private SortedSet<Double> nbrOfRestNotesInBeat;
    private double nbrOfBeats;
    private double nbrOfRestNotes;
    
    public MelodyRestDensityRater(double weight){
        super.setWeight(weight);
    }
    

    @Override
    public double rate(Song song) {
        /**Set global variables*/
        nbrOfRestNotesInBeat = new TreeSet<Double>();
        nbrOfBeats = 0;
        nbrOfRestNotes = 0;
        
        /**For every track tagged with Melody, check rest note density*/
        for(Track track : song.getTaggedTracks(TrackTag.MELODY)){
            checkPart(track.getPart()); 
        }

        /**If nbr of total rest notes in Melody tracks is 0, return rating 0*/
        if(nbrOfRestNotes <= 0 || nbrOfBeats <= 0){
            return 0;
        }
        
        /**Calculate average rest note density for all beats and
         * retrieve the least rest note density beat found*/
        double avarageNotesInBeat = (nbrOfRestNotes/nbrOfBeats);   
        double leastNotesInBeat = nbrOfRestNotesInBeat.first();

        /**Return least rest note density divided by the average rest note density*/
        return (leastNotesInBeat / avarageNotesInBeat);
    }
    
    /**Checks all rest notes in all phrases and counts 
     * rest note density for every beat
     * @param Part part to be checked for note density in every beat*/
    private void checkPart(Part part){
        
        /**Set variables to keep track of nbr of rest notes in a beat and 
         * to keep track when a full beat has been reached*/
        double restNotesInBeat = 0;
        double beatCheck = 0;
        
        /**Iterate through every phrase and rest note in part*/
        for(Phrase phrase : part.getPhraseArray()){
            for(Note note : phrase.getNoteArray()){
                
                /**If notes pitch value is not rest, skip to next note*/
                if(note.getPitch() != Note.REST)
                    continue;
                
                /**increase nbr of rest notes in current beat by 1*/
                restNotesInBeat++;
                
                /**Increase beatCheck by this notes rhythm value*/
                beatCheck += note.getRhythmValue();
                
                /**If beat check variable is equal to or larger than 1.0
                 * meaning if one beat has been reached or surpassed*/
                if(beatCheck >= 1.0){  
                    
                    /**Add nbr of rest notes in this beat to global variable*/
                    nbrOfRestNotesInBeat.add(restNotesInBeat);
                    
                    /**Increase global variable of total nbr of rest notes
                     * with nbr of rest notes in this beat*/
                    nbrOfRestNotes += restNotesInBeat;
                    
                    /**Increase global variable of total nbr of beats by 1*/
                    nbrOfBeats++;
                    
                    /**Re-set rest notes in beat variable to 0 for next iteration*/
                    restNotesInBeat = 0;
                    
                    /**Remove one beat from beat check variable*/
                    beatCheck -= 1.0;
                    
                    /**If beat check variable is still larger than 1, 
                     * meaning that the last rest notes rhythm value was larger than 1
                     * keep increasing total nbr of rest notes and total nbr of beats 
                     * and set 1 to nbr of rest notes in every beat
                     * as long as the beat check variable is larger than 1*/
                    while(beatCheck > 1){
                        nbrOfRestNotesInBeat.add(1.0);
                        nbrOfRestNotes++;
                        nbrOfBeats++;
                        beatCheck -= 1.0;
                    }
                    
                }
            }
            
            /**If there is rest notes in beat when phrase is done,
             * there are rest notes at the end that did not stretch a whole beat 
             * and we increase beat count and add nbr of rest note to total*/
            if(restNotesInBeat > 0){
                nbrOfRestNotesInBeat.add(restNotesInBeat);
                nbrOfRestNotes += restNotesInBeat;
                nbrOfBeats += beatCheck;
                restNotesInBeat = 0;
                beatCheck = 0;
            }
        }   
    }

}
