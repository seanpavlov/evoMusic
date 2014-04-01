package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.tools.ChordAnalysis;

import com.evoMusic.model.Song;
import com.evoMusic.util.TrackTag;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;

public class ChordRepetitionRater extends SubRater{
    
    public ChordRepetitionRater(double weight){
        super.setWeight(weight);
    }
 
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
        System.out.println("Rating: " + rating/count);
        return (rating/count);
    }
    
    public double ratePart(Part part){
        String valuesAsString = "";
        Phrase[] phrases = part.getPhraseArray();
        ListMultimap<Double, Integer> notesPitches = ArrayListMultimap.create();
        for(Phrase phrase : phrases){
            Note[] notes = phrase.getNoteArray();
            int nbrOfPitches = notes.length;
            if(nbrOfPitches == 0)
                continue;
            double pStart = phrase.getStartTime();
            for(Note note : notes){
                if(note.getPitch() == Note.REST)
                    continue;
                pStart = pStart + note.getRhythmValue();
                notesPitches.put(pStart, note.getPitch());
            }
        }        
        List<Chord> chordArray = new ArrayList<Chord>();
        List<Double> keys = new ArrayList<Double>(notesPitches.keySet());
        for(Double d : keys){
            List<Integer> pitches = notesPitches.get(d);
            for(Integer i : pitches){
                valuesAsString = valuesAsString + i + " ";
            }
            valuesAsString = valuesAsString + " ";
            chordArray.add(new Chord(pitches, d));
        }    
        
        double before = (double)valuesAsString.replaceAll("\\s+","").length();
        int minimum = (int)(before*0.01);
        
        List<List<Chord>> foundChordPatterns = findChordPatterns(chordArray, minimum);
        
        Map<Integer, List<String>> occurrence = new HashMap<Integer, List<String>>();
        for(List<Chord> cList : foundChordPatterns){
            String nextChordPattern = "";
            for(Chord c : cList){
                nextChordPattern = nextChordPattern + c.toString();
                nextChordPattern = nextChordPattern + " ";
            }
            Integer s = (this.numberOfOccurrences(nextChordPattern, valuesAsString) * nextChordPattern.length());
            List<String> strings = occurrence.get(s);
            if(strings == null){
                strings = new ArrayList<String>();
                strings.add(nextChordPattern);
                occurrence.put(s, strings);
            }else{
                strings.add(nextChordPattern);
                occurrence.put(s, strings);
            }
            
            ArrayList<Integer> intKeys = new ArrayList<Integer>(occurrence.keySet());
            Collections.sort(intKeys, Collections.reverseOrder());
            for(Integer k : intKeys){
                List<String> p = occurrence.get(k);
                for(String sC : p){
                    if(this.numberOfOccurrences(sC, valuesAsString) >= 2)
                        valuesAsString = valuesAsString.replace(sC, "");
                }
            }
            
            
                
        }        
        double after = (double)valuesAsString.replaceAll("\\s+","").length();
        
        return 1 - (after/before);
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
            String toString = "";
            for(Integer pitch : this.notes){
                toString = toString + pitch + " ";
            }
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
    
    /**
     * Returns the number of occurrences of a string pattern in string 
     * @param pattern String pattern to count occurrences of
     * @param content String content to search string pattern in
     * */
    private int numberOfOccurrences(String pattern, String content){
        return content.split(Pattern.quote(pattern), -1).length - 1;
    }
    
}
