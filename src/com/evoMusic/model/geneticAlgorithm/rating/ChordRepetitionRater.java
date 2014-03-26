package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.google.common.collect.Range;

public class ChordRepetitionRater extends SubRater{
 
    @Override
    public double rate(Song song) {
        double rating = 0;
        double count = 0;
        for(Part part : song.getTaggedTracks(TrackTag.CHORDS)){
            rating += this.ratePart(part);
            ++count;
        }
        if(count == 0)
            return 0;
        return rating/count;
    }
    
    public double ratePart(Part part){
        Phrase[] phrases = part.getPhraseArray();
        Map<Double, List<Integer>> notePitches = new HashMap<Double, List<Integer>>();
        //Map<Range<Double>, List<Note>> chords = new HashMap<Range<Double>, List<Note>>();
        Map<Double, List<Note>> chords = new HashMap<Double, List<Note>>();
        
        System.out.println(phrases.length);
        for(Phrase phrase : phrases){
            Note[] notes = phrase.getNoteArray();
            double pStart = phrase.getStartTime();
            for(int i = 0; i < notes.length; i++){
                Note note = notes[i];
                
                if(note.getPitch() < 0)
                    continue;
                double start = phrase.getNoteStartTime(i) + pStart;
                //Range<Double> range = Range.closed(start, (start + note.getDuration()));
                List<Integer> np = notePitches.get(start);
               // List<Note> cn = chords.get(range);
                List<Note> cn = chords.get(start);
                if(cn != null){
                    np.add(note.getPitch());
                    notePitches.put(start, np);
                    
                    cn.add(note);
                    //chords.put(range, cn);
                    chords.put(start, cn);
                }else{
                    np = new ArrayList<Integer>();
                    cn = new ArrayList<Note>();
                    np.add(note.getPitch());
                    notePitches.put(start, np);
                    
                    cn.add(note);
                    //chords.put(range, cn);  
                    chords.put(start, cn);
                }
                /*for(Range<Double> r : chords.keySet()){
                    if((!range.equals(r)) && range.isConnected(r)){
                        List<Note> cnotes = chords.get(r);
                        cnotes.add(note);
                        chords.put(r, cnotes);
                    }
                }*/
            }
        }
        
        List<Chord> chordArray = new ArrayList<Chord>();
        //Set<Range<Double>> keys = chords.keySet();
        Set<Double> keys = chords.keySet();
        //List<Double> k = new ArrayList<Double>(keys);
        //Collections.sort(k);
        System.out.println("Keys: " + keys.size());
        for(Double d : keys){
            List<Note> chord = chords.get(d);
            List<Integer> chordPitches = new ArrayList<Integer>();
            //chordArray.add(new Chord(chord,d));
            //System.out.print("Chord: ");
            for(Note n : chord){
                //System.out.print(n.getNote() + "(pitch:"+ n.getPitch() +", Dur: " + n.getDuration() +") ");
                chordPitches.add(n.getPitch());
            }
            chordArray.add(new Chord(chordPitches, d));
           // System.out.println();
        }    
        
        List<List<Chord>> foundChordPatterns = findChordPatterns(chordArray, 5);
        
        return 0;
    }

    
    public List<List<Chord>> findChordPatterns(List<Chord> chords, int min){
        
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
        
        this.sortByValues(suffixList);
        
        List<Chord> nextFound = new ArrayList<Chord>();
        
        int distance;
        int checkFurter = 1;
        
        for(int i = 1; i < inputLength; i++){
            List<Chord> firstList = suffixList.get(i);
            for(int n = checkFurter; n >= 1; n--){
                
                List<Chord> secondList = (i >= checkFurter) ? 
                        suffixList.get(i-checkFurter) : suffixList.get(i);
                
                distance = Math.abs(firstList.size() - secondList.size());               
                if(distance < min){                    
                    checkFurter = 
                            (firstList.size() >= min  &&
                             secondList.size() >= min &&
                             firstList.subList(0, min).equals(secondList.subList(0, min))) ?
                                     Math.max(checkFurter, n+1) : n;
                    continue;
                }
                
                /** if next suffixes don't at least match or is as long as the best,
                 *  no need to check more carefully
                 */
                if(!(firstList.size() >= min) || 
                   !(secondList.size() >= min) ||
                   !firstList.subList(0, min).equals(secondList.subList(0, min))){
                    checkFurter = n;
                    continue;
                }
                
                nextFound = this.longestCommon(firstList, secondList, distance);
                bestResults.add(0, nextFound);
                
                checkFurter = (nextFound.size() == distance) ?
                        Math.max(checkFurter, n+1) : n;                
            }
            
        }
        this.sortBySize(bestResults);
        return bestResults;      
    }
    
    public class Chord{
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
        
        public int lessThan(Chord chord){
            int minSize =  Math.min(this.notes.size(), chord.notes.size());
            for(int i = 0; i < minSize; i++){
                Integer firt = chord.notes.get(i);
                Integer second = this.notes.get(i);
                if(firt < second)
                    return 1;
                else if(firt > second)
                    return -1;
            }
            if(chord.notes.size() < this.notes.size())
                return 1;
            else if(chord.notes.size() > this.notes.size())
                return -1;
            return 0;
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
        
        @Override
        public String toString(){
            String toString = "Chord: ( ";
            for(Integer pitch : this.notes){
                toString = toString + pitch + " ";
            }
            toString = toString + ")";
            return toString;
        }
    }
    
    
    public void sortByValues(List<List<Chord>> list){
        Collections.sort(list,new Comparator<List<Chord>>() {
            public int compare(List<Chord> values, List<Chord> otherValues) {
                int minSize =  Math.min(values.size(), otherValues.size());
                for(int i = 0; i < minSize; i++){
                    Chord first = values.get(i);
                    Chord second = otherValues.get(i);
                    int r = first.lessThan(second);
                    if(r != 0)
                        return r;
                }
                if(values.size() < otherValues.size())
                    return -1;
                else if(values.size() > otherValues.size())
                    return 1;
                else
                    return 0;
            }
        });
    }
    
    private List<Chord> longestCommon(List<Chord> first, List<Chord> second, int max){
        int n = Math.min(first.size(), second.size());
        if(max < n){
            n = max;
        }
        List<Chord> longestCommon = new ArrayList<Chord>();
        for(int i = 0; i < n; i++){
            if(!first.get(i).equals(second.get(i))){
                return longestCommon;
            }
            longestCommon.add(first.get(i));
        }
        return longestCommon;
    }
    
    private void sortBySize(List<List<Chord>> list){
        Collections.sort(list, new Comparator<List<Chord>>(){
            public int compare(List<Chord> values, List<Chord> otherValues){
                return otherValues.size() - values.size();
            }
        });
    }
    
    private List<Chord> removeOccurence(List<Chord> pattern, List<Chord> chords){
        int pointer = 0;
        for(Chord c : chords){
            if(c.equals(pattern.get(pointer))){
                
            }
        }
        return null;
    }
    
}
