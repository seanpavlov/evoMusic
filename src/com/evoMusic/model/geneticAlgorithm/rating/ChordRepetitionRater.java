package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.tools.ChordAnalysis;

import com.evoMusic.model.Song;
import com.evoMusic.util.TrackTag;

public class ChordRepetitionRater extends SubRater{
 
    @Override
    public double rate(Song song) {
        double rating = 0;
        
        for(Part part : song.getTaggedTracks(TrackTag.CHORDS)){
            rating += this.ratePart(part);
        }
        return rating;
    }
    
    public double ratePart(Part part){
        Phrase[] phrases = part.getPhraseArray();
        Map<Double, List<Note>> chords = new HashMap<Double, List<Note>>();
        
        for(Phrase phrase : phrases){
            Note[] notes = phrase.getNoteArray();
            for(Note note : notes){
                double start = note.getSampleStartTime();
                List<Note> cn = chords.get(start);
                if(cn != null){
                    cn.add(note);
                    chords.put(start, cn);
                }else{
                    cn = new ArrayList<Note>();
                    cn.add(note);
                    chords.put(start, cn);
                }                   
            }
        }
        
        Set<Double> keys = chords.keySet();
        for(Double d : keys){
            List<Note> chord = chords.get(d);
            System.out.println("Chord: ");
            for(Note n : chord){
                System.out.print(n.getNote() + " ");
            }
        }
        
        
        return 0;
    }

}
