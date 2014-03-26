package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.Collections;
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
        Map<Double, List<Integer>> notePitches = new HashMap<Double, List<Integer>>();
        Map<Double, List<Note>> chords = new HashMap<Double, List<Note>>();
        System.out.println(phrases.length);
        for(Phrase phrase : phrases){
            Note[] notes = phrase.getNoteArray();
            double pStart = phrase.getStartTime();
            for(int i = 0; i < notes.length; i++){
                Note note = notes[i];
                if(note.getNote().equals("N/A"))
                    continue;
                double start = phrase.getNoteStartTime(i) + pStart;
                List<Integer> np = notePitches.get(start);
                List<Note> cn = chords.get(start);
                if(cn != null && !np.contains(note.getPitch())){
                    np.add(note.getPitch());
                    notePitches.put(start, np);
                    
                    cn.add(note);
                    chords.put(start, cn);
                }else{
                    np = new ArrayList<Integer>();
                    cn = new ArrayList<Note>();
                    np.add(note.getPitch());
                    notePitches.put(start, np);
                    
                    cn.add(note);
                    chords.put(start, cn);
                    
                }                   
            }
        }
        
        List<Chord> chordArray = new ArrayList<Chord>();
        Set<Double> keys = chords.keySet();
        List<Double> k = new ArrayList<Double>(keys);
        Collections.sort(k);
        System.out.println("Keys: " + keys.size());
        for(Double d : k){
            List<Note> chord = chords.get(d);
            //chordArray.add(new Chord(chord,d));
            System.out.print("Chord: ");
            for(Note n : chord){
                System.out.print(n.getNote() + "(pitch:"+ n.getPitch() +", Dur: " + n.getDuration() +") ");
                
            }
            System.out.println();
        }    
        
        List<List<Chord>> foundChordPatterns = findChordPatterns(chordArray, 5);
        
        return 0;
    }

    
    private List<List<Chord>> findChordPatterns(List<Chord> chords, int min){
        
        /**Best result and suffix lists*/
        List<List<Chord>> bestResults = new ArrayList<List<Chord>>();
        List<List<Chord>> suffixList = new ArrayList<List<Chord>>();
        
        int inputLength = chords.size();
        
        for(int i = 0; i < inputLength; i++){
            List<Chord> nextSuffix = chords.subList(i, inputLength);
            if(nextSuffix.size() >= min)
                suffixList.add(nextSuffix);
        }
        
        inputLength = suffixList.size();
        
        List<Chord> nextFound = new ArrayList<Chord>();
        
        int distance;
        int checkFurter = 1;
        
        
        return bestResults;       
    }
    
    protected class Chord{
        List<Integer> notes;
        double duration;
        
        public Chord(List<Integer> notes, double duration){
            this.notes = new ArrayList<Integer>();
            this.notes = notes;
            this.duration = duration;
        }
        
        public Chord(){
            this.notes = new ArrayList<Integer>();
        }
        
        public void addNote(Integer pitch, double duration){
            this.notes.add(pitch);
            if(this.duration < duration)
                this.duration = duration;
        }
        
        @Override 
        public boolean equals(Object o){
            if(!(o instanceof Chord))
                return false;
            Chord c = (Chord)o;
            if(!(c.notes.size() == notes.size()))
                return false;
            for(int i = 0; i < notes.size(); i++){
                if(!(c.notes.get(i).equals(notes.get(i))))
                    return false;
            }
            return true;
        }
    }
    
}
