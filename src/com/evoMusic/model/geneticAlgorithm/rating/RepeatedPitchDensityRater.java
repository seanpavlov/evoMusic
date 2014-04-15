package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.Sort;
import com.evoMusic.util.TrackTag;

public class RepeatedPitchDensityRater extends SubRater{
    
    public RepeatedPitchDensityRater(double weight){
        super.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        double partRating = 0;
        double nbrOfParts = 0;
        
        for(Track track : song.getTaggedTracks(TrackTag.MELODY)){
            partRating += ratePart(track.getPart());
            nbrOfParts++;
        }
        
        if(nbrOfParts == 0)
            return 0;
        
        return partRating/nbrOfParts;
    }
    
    private double ratePart(Part part){
        List<List<Note>> sortedNotes = Sort.getSortedNoteList(part);
        double twoInRow = 0;
        double nbrOfNotes = 0;
        for(int i = 0; i < sortedNotes.size() - 1; i++){
            List<Note> currentNotes = sortedNotes.get(i);
            List<Note> nextNotes = sortedNotes.get(i+1);
            for(Note currentNote : currentNotes){
                int currentPitch = currentNote.getPitch();
                if(currentPitch == Note.REST)
                    continue;
                for(Note nextNote : nextNotes){
                    int nextPitch = nextNote.getPitch();
                    if(nextPitch == Note.REST)
                        continue;
                    if(currentPitch == nextPitch)
                        twoInRow++;
                }
                nbrOfNotes++;
            }
        }
        
        nbrOfNotes += sortedNotes.get(sortedNotes.size() - 1).size();
        
        if(nbrOfNotes == 0 || twoInRow == 0)
            return 0;
        
        if(nbrOfNotes - twoInRow == 1)
            return 1.0;
        
        return twoInRow / nbrOfNotes ;
    }

}
