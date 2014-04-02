package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;
import jm.music.data.Note;
import jm.music.data.Part;

import com.evoMusic.model.Song;
import com.evoMusic.util.Sort;

public class CrazyNoteOctaveRater extends SubRater{
    
    public CrazyNoteOctaveRater(double weight){
        super.setWeight(weight);
    }

    public double rate(Song song){
        double rating = 0;
        Part[] parts = song.getScore().getPartArray();
        for(Part part : parts){
            rating += this.ratePart(part);
        }
    
        System.out.println("Rating ocatave: " + rating/(double)parts.length);
        
        if(rating < 0)
            return 0;
        return rating/(double)parts.length;
    }
    
    
    private double ratePart(Part part){
        List<List<Note>> sortedNotes = Sort.getSortedNoteList(part);
        double rating = 1.0;
        double amount = 0;
        double crazyCount = 0;
        if(sortedNotes.size() > 0){
        int lastNote = sortedNotes.get(0).remove(0).getPitch();
        for(List<Note> notes : sortedNotes){
            for(Note note : notes){
                int thisNote = note.getPitch();
                int difference = Math.abs(lastNote - thisNote);
                if( difference >= 120){
                    rating -= 0.01;
                    crazyCount++;
                }else if(difference >= 108){
                    rating -= 0.009;
                    crazyCount++;
                }else if(difference >= 96){
                    rating -= 0.008;
                    crazyCount++;
                }else if(difference >= 84){
                    rating -= 0.007;
                    crazyCount++;
                }else if(difference >= 72){
                    rating -= 0.006;
                    crazyCount++;
                }else if(difference >= 60){
                    rating -= 0.005;
                    crazyCount++;
                }else if(difference >= 60){
                    rating -= 0.004;
                    crazyCount++;
                }else if(difference >= 48){
                    rating -= 0.003;
                    crazyCount++;
                }else if(difference >= 36){
                    rating -= 0.002;
                    crazyCount++;
                }else if(difference >= 24){
                    rating -= 0.001;
                    crazyCount++;
                }
                amount++;
                lastNote = thisNote;
            }
        } 
        }
        double amountOf = crazyCount/amount;
        if(amountOf >= rating)
            return rating;
        return (rating - amountOf);
    }
    
    
}
