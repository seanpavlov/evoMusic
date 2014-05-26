package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.parameters.P;
import com.evoMusic.util.TrackTag;

public class MelodyDirectionStabilityRater extends SubRater{
    /**Rater that likes more pitch direction changes in melody tracks*/
    
    public MelodyDirectionStabilityRater(double targetRating){
        super.setTargetRating(targetRating);
        super.setInfluenceMultiplier(P.RATER_MELODY_DIRECTION_STABILITY_INFLUENCE_MUL);
    }

    @Override
    public double rate(Song song) {
        /**Initiate phrase and total rating values*/
        double partRatings = 0;
        double rating = 0;
        /**Rate every track tagged with MELODY*/
        List<Track> tracks = song.getTaggedTracks(TrackTag.MELODY);
        double nbrOfMelodyTracks = tracks.size();
        for(Track track : tracks){
            partRatings += this.ratePart(track.getPart());
        }
        
        /**Song with no melody track or
         * every part got rating 0 total rating is 0
         * otherwise calculate rating by total rating of parts
         * divided by total nbr of parts to get median rating
         * */
        return (nbrOfMelodyTracks == 0 || partRatings == 0) ?
                    rating : partRatings/nbrOfMelodyTracks;
    }
    
    
    /**
     * Rates part by how many pitch direction changes their is over all notes in part
     * @param Part part to be rated
     * */
    private double ratePart(Part part){
        /**Set direction to intital value false (false = down, true = up)*/
        boolean direction = false;
        /**Declare variables to keep track of pitch direction changes and number of total notes*/
        double changes = 0;
        double nbrOfNotes = 0;
        /**Iterate through every phrase in part*/
        for(Phrase phrase : part.getPhraseArray()){
            Note[] notes = phrase.getNoteArray();
            /**If nbr of notes in part is less than or equal to 2, their can be no direction changes
             * and we add nbr of notes whose pitch value is not rest to nbrOfNotes counter variable
             * and skip to next phrase
             *  */
            if(notes.length <= 2){
                for(Note note : notes){
                    if(note.getPitch() != Note.REST)
                        nbrOfNotes++;
                }
                continue;
            }
              
            
            /**startNote counter variable to keep track of
            * where to start to iterate after finding 
            * initialNote and first 'lastNote' whose pitch
            * values where not rest
            * */
            int startNote = 0;
            int intitialNotePitch;
            int lastNotePitch;
                
            /**Find initial note whose pitch value is not rest*/
            do{
                intitialNotePitch = notes[startNote].getPitch(); 
                startNote++;
            }while(intitialNotePitch == Note.REST &&
                    startNote < notes.length);
                
            /**If initial note found whas last note in phrase, skip to next phrase*/
            if(startNote >= notes.length)
                continue;
                
            /**Find first 'lastNote' whose pitch values is not rest
            * to calculate initial pitch direction
            * */
            do{            
                lastNotePitch = notes[startNote].getPitch();
                startNote++;
            }while(lastNotePitch == Note.REST &&
                    startNote < notes.length);
                
            /**If first 'lastNote' found was last note in phrase, skip to next phrase*/
            if(startNote >= notes.length)
                continue;
                     
            /**Increase nbrOfNotes by 2 to count whith inital and last note*/
            nbrOfNotes += 2;
                
            /**If inital and last note has pitch direction up, set direction variable to true*/
            if(intitialNotePitch <= lastNotePitch)
                direction = true;
                
            /**Iterate through rest of notes to calculate nbr of direction changes*/
            for(int i = startNote; i < notes.length; i++){
                int currentNotePitch = notes[i].getPitch();
                
                /**If current note is a rest, skip to next note*/
                if(currentNotePitch == Note.REST)
                    continue;
                
                /**Calculate next note direction*/
                boolean currentDirection = lastNotePitch <= currentNotePitch;
                
                /**If current note direction is different than last, increase count and save current direction*/
                if(direction != currentDirection){
                    changes++;
                    direction = currentDirection;
                }
                
                /**Save current note and increase note count*/
                lastNotePitch = currentNotePitch;    
                nbrOfNotes++;
           }
        }
        
        
        /**Best case is 2 less changes than total nbr of notes, return rating 1.0
         * Otherwise calculate rating by number of changes in pitch direction 
         * compared to total nbr of non rest notes in part
         * */
        return ((nbrOfNotes - changes) == 2.0) ? 1.0 : changes/nbrOfNotes;
    }
}
