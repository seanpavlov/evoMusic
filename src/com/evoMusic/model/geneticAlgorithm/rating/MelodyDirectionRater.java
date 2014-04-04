package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

public class MelodyDirectionRater extends SubRater{

    @Override
    public double rate(Song song) {
        double rating = 0;
        List<Track> tracks = song.getTaggedTracks(TrackTag.MELODY);
        for(Track track : tracks){
            rating += this.ratePart(track.getPart());
        }
        return rating/tracks.size();
    }
    
    private double ratePart(Part part){
        boolean directionUp = false;
        double changes = 0;
        double nbrOfNotes = 0;
        for(Phrase phrase : part.getPhraseArray()){
            Note[] notes = phrase.getNoteArray();
            if(notes.length > 2){
                Note intialNote = notes[0];
                Note lastNote = notes[1];
                nbrOfNotes += 2;
                if(intialNote.getPitch() <= lastNote.getPitch())
                    directionUp = true;
                
                for(int i = 2; i < notes.length; i++){
                    Note note = notes[i];
                    boolean direction = lastNote.getPitch() <= note.getPitch();
                    if(directionUp != direction){
                        changes++;
                        directionUp = direction;
                    }
                    lastNote = note;    
                    nbrOfNotes++;
                }
            }
        }
        
        double rateValue = changes/nbrOfNotes;
        
        return rateValue;
    }

}
