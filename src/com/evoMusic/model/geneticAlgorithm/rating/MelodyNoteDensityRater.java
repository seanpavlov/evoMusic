package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.SortedSet;
import java.util.TreeSet;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

/**Rater that favors song where the average note density in 
 * all beats in it's melody tracks are close or eqaul
 * to the least note density beat found in songs melody tracks*/
public class MelodyNoteDensityRater extends SubRater{
    
    /**Global variables to keep nbr of notes in different beats,
     * total nbr of beats and total nbr of notes 
     * */
    private SortedSet<Double> nbrOfNotesInBeat;
    private double nbrOfBeats;
    private double nbrOfNotes;

    public MelodyNoteDensityRater(double weight){
        super.setWeight(weight);
    }
    
    @Override
    public double rate(Song song) {
        /**Set global variables*/
        nbrOfNotesInBeat = new TreeSet<Double>();
        nbrOfBeats = 0;
        nbrOfNotes = 0;
        
        /**For every track tagged with Melody, check note density*/
        for(Track track : song.getTaggedTracks(TrackTag.MELODY)){
            checkPart(track.getPart()); 
        }
        
        System.out.println("Nbr Of notes: " + nbrOfNotes);
        /**If nbr of total notes in Melody tracks is 0, return rating 0*/
        if(nbrOfNotes == 0 || nbrOfBeats == 0)
            return 0;
        
        /**Calculate average note density for all beats and
         * retrieve the least note density beat found*/
        double avarageNotesInBeat = (nbrOfNotes/nbrOfBeats);   
        double leastNotesInBeat = nbrOfNotesInBeat.first();

        System.out.println("Rating density: " + leastNotesInBeat / avarageNotesInBeat);
        /**Return least note density divided by the average note density*/
        return (leastNotesInBeat / avarageNotesInBeat);
    }
    
    /**Checks all notes in all phrases and counts 
     * note density for every beat
     * @param Part part to be checked for note density in every beat*/
    private void checkPart(Part part){
        
        /**Set variables to keep track of nbr of notes in a beat and 
         * to keep track when a full beat has been reached*/
        double notesInBeat = 0;
        double beatCheck = 0;
        
        /**Iterate through every phrase and note in part*/
        for(Phrase phrase : part.getPhraseArray()){
            for(Note note : phrase.getNoteArray()){
                
                /**If notes pitch value is rest, skip to next note*/
                if(note.getPitch() == Note.REST)
                    continue;
                
                /**increase nbr of notes in current beat by 1*/
                notesInBeat++;
                
                /**Increase beatCheck by this notes rhythm value*/
                beatCheck += note.getRhythmValue();
                
                /**If beat check variable is equal to or larger than 1.0
                 * meaning if one beat has been reached or surpassed*/
                if(beatCheck >= 1.0){  
                    
                    /**Add nbr of notes in this beat to global variable*/
                    nbrOfNotesInBeat.add(notesInBeat);
                    
                    /**Increase global variable of total nbr of notes
                     * with nbr of notes in this beat*/
                    nbrOfNotes += notesInBeat;
                    
                    /**Increase global variable of total nbr of beats by 1*/
                    nbrOfBeats++;
                    
                    /**Re-set notes in beat variable to 0 for next iteration*/
                    notesInBeat = 0;
                    
                    /**Remove one beat from beat check variable*/
                    beatCheck -= 1.0;
                    
                    /**If beat check variable is still larger than 1, 
                     * meaning that the last notes rhythm value was larger than 1
                     * keep increasing total nbr of notes and total nbr of beats 
                     * and set 1 to nbr of notes in every beat
                     * as long as the beat check variable is larger than 1*/
                    while(beatCheck > 1){
                        nbrOfNotesInBeat.add(1.0);
                        nbrOfNotes++;
                        nbrOfBeats++;
                        beatCheck -= 1.0;
                    }
                    
                }
            }
            
            /**If there is notes in beat when phrase is done,
             * there are notes at the end that did not stretch a whole beat 
             * and we increase beat count and add nbr of note to total*/
            if(notesInBeat > 0){
                nbrOfNotesInBeat.add(notesInBeat);
                nbrOfNotes += notesInBeat;
                nbrOfBeats += beatCheck;
                notesInBeat = 0;
                beatCheck = 0;
            }
        }   
    }
}
