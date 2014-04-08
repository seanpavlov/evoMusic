package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

/**This rater favors melodies where the overall pitch direction
 * is up*/
public class MelodyDirectionRater extends SubRater{
    
    public MelodyDirectionRater(double weight){
        super.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        /**Set variable for total part ratings*/
        double partRatings = 0.0;
        /**Retrive every melody track and set nbroftrack variable*/
        List<Track> tracks = song.getTaggedTracks(TrackTag.MELODY);
        double nbrOfTracks = tracks.size();
        /**For every melody track rate track and add to partRatings*/
        for(Track track : tracks){
            partRatings += ratePart(track.getPart());
        }

        /**If partRatings has value 0, return rating 0, 
         * otherwise return partRatings 
         * divided by nbrOfTracks to get median rating of all parts
         * */
        return (partRatings == 0) ? 0 : (partRatings / nbrOfTracks);
    }
    
    private double ratePart(Part part){
        
        /**Declare variable to keep track of how many times pitch values is going up 
         * and nbr of total amount of notes not a rest*/
        double upCount = 0;
        double nbrOfNotes = 0;
        
        /**Declare variable to hold last pitch value*/
        int lastPitch;
        /**Iterate through every phrase of part*/
        for(Phrase phrase : part.getPhraseArray()){
            /**Declare variable to keep track of where to start iteration
             * after finding first note not with rest pitch value*/
            int startNote = 0;
            Note[] notes = phrase.getNoteArray();
            int nbrOfNotesInPhrase = notes.length;
            
            /**If there is 1 note in phrase, skip to next*/
            if(nbrOfNotesInPhrase < 2)
                continue;
            
            do{
                lastPitch = notes[startNote].getPitch();
                startNote++;
            }while(lastPitch == Note.REST &&
                    startNote < nbrOfNotesInPhrase);
            
            /**If the first note found without pitch value == rest 
             * is the last note in phrase, skip to next phrase*/
            if(startNote == nbrOfNotesInPhrase)
                continue;
            
            /**Iterate through notes from startNote point, if note is not
             * a rest and it's pitch value is bigger than lastnote 
             * increase upCount and save current note as last note 
             * and increase nbrOfNotes count*/
            for(int position = startNote; position < notes.length; position++){
                int currentPitch = notes[position].getPitch();
                if(currentPitch == Note.REST){
                    continue;
                }else if(lastPitch < currentPitch){
                    upCount++;
                }  
                lastPitch = currentPitch;
                nbrOfNotes++;   
            }
        }     
        
        /**If upCount is 0, return part rating 0,
         * Or if upCount is 1 less than nbr of total notes return part rating 1
         * otherwise return upCount divided by nbr of total notes*/
        return (upCount == 0) ? 0 : 
               (nbrOfNotes - upCount == 1) ? 1 : 
               (upCount / nbrOfNotes);
    }

}
