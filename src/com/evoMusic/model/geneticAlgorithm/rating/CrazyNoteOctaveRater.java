package com.evoMusic.model.geneticAlgorithm.rating;


import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.parameters.P;
import com.evoMusic.util.Sort;
import com.evoMusic.util.TrackTag;

public class CrazyNoteOctaveRater extends SubRater{
    private final int OCTAVE_CONSTANT = 24;
    
    public CrazyNoteOctaveRater(double targetRating) {
        super.setTargetRating(targetRating);
        super.setInfluenceMultiplier(P.RATER_CRAZY_OCTAVE_INFLUENCE_MUL);
    }

    public double rate(Song song){
        double partRating = 0;
        double nbrOfParts = 0;
        for(Track track : song.getTaggedTracks(TrackTag.MELODY)){
            partRating += ratePart(track.getPart());
            nbrOfParts++;
        }
        return(nbrOfParts == 0) ? 0 : partRating/nbrOfParts;     
    }
    
    private double ratePart(Part part){
        /**Get list of lists of sorted notes by their start time*/
        List<List<Note>> sortedNotesLists = Sort.getSortedNoteList(part);
        
        /**Counters to keep track of how many crazy notes found and total number of notes*/
        double amountOfNotesChecked = 0;
        double crazyCount = 0;
        
        /**Iterate through every sorted notes list*/
        for(int position = 0; position < sortedNotesLists.size() - 1; position++){
            List<Note> notes = sortedNotesLists.get(position);
            /**For every note with current start time, check against every note in next start time list*/
            for(Note currentNote :  notes){
                /**If current note is in rest, jump to next itaration*/
                int currentNotePitch = currentNote.getPitch();
                if(currentNotePitch == Note.REST)
                    continue;
                for(Note nextNote : sortedNotesLists.get(position + 1)){
                    int nextNotePitch = nextNote.getPitch();
                    /**If next note is in rest, jump to next itaration*/
                    if(nextNotePitch == Note.REST)
                        continue;
                    /**Calculate difference in current note pitch and next note pitch*/
                    int difference = Math.abs(currentNotePitch - nextNotePitch);
                    if(difference >= OCTAVE_CONSTANT)
                        crazyCount++;    
                    amountOfNotesChecked++;
                }
                /**Keep track of how many note in total*/
                
            }
        }
        
        if(crazyCount == 0)
            return 1.0;
        
        return (1.0 - crazyCount/amountOfNotesChecked);
    }
}
