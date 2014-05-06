package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import jm.music.data.Note;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.Sort;

public class RhythmValueMutator extends ISubMutator {
    double movingRange;
    
    public RhythmValueMutator(double mutationProbability, double movingRange) {
        super(mutationProbability);
        this.movingRange = movingRange;
    }

    @Override
    public void mutate(Song individual) {
        Random ra = new Random();

        for (Track track : individual.getTracks()) {
            List<List<Note>> noteList = Sort.getSortedNoteList(track.getPart());
            
            // For all elements in the list
            for (List<Note> parallelNoteList : noteList) {
                
                //if probability allows this note to be mutated
                if (Math.random() < getProbability()) {
                    double movingLength = 0.5 * (ra.nextInt(((int)(movingRange/0.5)))+1);
                    if(Math.random() < 0.5){
                        movingLength = -movingLength;
                    }
                    
                    // For all parallel notes
                    for (Note note : parallelNoteList) {
                        double newStartTime = 
                                note.getMyPhrase().getStartTime() + 
                                note.getMyPhrase().getNoteStartTime(note.getMyPhrase().getNoteList().indexOf(note)) + 
                                movingLength;
                        if(!note.isRest()){
                            if(newStartTime >= 0 && newStartTime + note.getDuration() <= individual.getScore().getEndTime()){
                                Phrase newPhrase = new Phrase(newStartTime);
                                newPhrase.addNote(note.copy());
                                track.getPart().addPhrase(newPhrase);
                                note.setPitch(Integer.MIN_VALUE);
                            }
                        }
                    }
                }
            }
        }
        
        individual.flattern();
    }
}
