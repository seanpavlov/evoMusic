package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.HashSet;
import java.util.Set;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

/**This rater is in favor of songs 
 * where there is many different note pitch values
 * */
public class PitchVarietyRater extends SubRater{
    /**Class global variables to keep track of different note pitches
     * and total nbr of notes
     * */
    private Set<Integer> pitches;
    private Integer nbrOfNotes;
    
    public PitchVarietyRater(double weight){
        super.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        /**Re-set set of different pitches and counter of total nbr of notes for every rating*/
        this.pitches = new HashSet<Integer>();
        this.nbrOfNotes = 0;
        /**Check pitches and nbr of notes for every part*/
        for(Track track : song.getTracks()){
            checkPart(track.getPart());
        }
        /**Set nbr of different note pitches found in song*/
        double nbrOfDifferentPitches = pitches.size();
        
        /**If nbr of different note pitches is 0, return rating 0
         * and if different note pitches is 1 and total nbr of notes is more than 1, return rating 0
         * otherwise return nbr of different note pitches found divided
         * by total nbr of notes in song
         * */
        return (nbrOfDifferentPitches == 0 || 
                (nbrOfDifferentPitches == 1 &&
                 nbrOfNotes > 1)) ? 0 : nbrOfDifferentPitches/nbrOfNotes;
    }
    
    /**Counts nbr of notes in part and add pitches to global set for each note if never seen before
     * @param Part part to check notes in
     * */
    private void checkPart(Part part){
        /**For every note in every phrase in part,
         * if notes pitch value is not rest
         * add to global set of different pitch values and 
         * increase count of global variable of total nbr of notes*/
        for(Phrase phrase : part.getPhraseArray()){
            for(Note note : phrase.getNoteArray()){
                int notePitch = note.getPitch();
                if(notePitch != Note.REST){
                    pitches.add(notePitch);
                    nbrOfNotes++;
                }                
            }
        }
    }

}
