package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

public class MelodyDirectionRater extends SubRater{
    /**Rater that likes more pitch direction changes in melody tracks*/
    
    public MelodyDirectionRater(double weight){
        super.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        /**Initiate rating value*/
        double rating = 0;
        /**Rate every track tagged with MELODY*/
        List<Track> tracks = song.getTaggedTracks(TrackTag.MELODY);
        int nbrOfMelodyTracks = tracks.size();
        for(Track track : tracks){
            rating += this.ratePart(track.getPart());
        }
        /**Song with no melody track gets rating 0*/
        if(nbrOfMelodyTracks == 0)
            return 0.0;
        
        /**Return median rating of all the rated parts*/
        return rating/nbrOfMelodyTracks;
    }
    
    private double ratePart(Part part){
        /**Set direction to intital value false (false = down, true = up)*/
        boolean direction = false;
        /**Declare variables to keep track of pitch direction changes and number of total notes*/
        double changes = 0;
        double nbrOfNotes = 0;
        /**Iterate through every phrase in part*/
        for(Phrase phrase : part.getPhraseArray()){
            Note[] notes = phrase.getNoteArray();
            if(notes.length > 2){
                int intitialNotePitch = notes[0].getPitch();
                int lastNotePitch = notes[1].getPitch();
                nbrOfNotes += 2;
                /**Set direction for initial and second note if direction is up*/
                if(intitialNotePitch <= lastNotePitch)
                    direction = true;
                /**Iterate through rest of notes to calculate nbr of direction changes*/
                for(int i = 2; i < notes.length; i++){
                    int currentNotePitch = notes[i].getPitch();
                    /**Calculate next note direction*/
                    boolean nextDirection = lastNotePitch <= currentNotePitch;
                    /**If next note direction is different then last, increase count and make next direction the current*/
                    if(direction != nextDirection){
                        changes++;
                        direction = nextDirection;
                    }
                    lastNotePitch = currentNotePitch;    
                    nbrOfNotes++;
                }
            }
        }
        /**Calculate rating by number of changes in direction compared to total nbr of notes*/
        return changes/nbrOfNotes;
    }
}
