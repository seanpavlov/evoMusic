package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class ChordRepetitionRater extends SubRater{
    
    public ChordRepetitionRater(double weight){
        super.setWeight(weight);
    }
 
    @Override
    public double rate(Song song) {
        double rating = 0;
        double count = 0;
        /**Rate every part tracked with CHORDS and return median rating value*/
        for(Track track : song.getTaggedTracks(TrackTag.CHORDS)){
            rating += this.ratePart(track.getPart());
            ++count;
        }  
        if(count == 0)
            return 0;
        return (rating/count);
    }
    
    public double ratePart(Part part){
        
        /**Sort every note in all the phrases in groups by their start time.
         * Saved in a ListMultimap with double (start time) as key and list
         * of Integer (pitch values) as value*/
        ListMultimap<Double, Integer> notesPitches = ArrayListMultimap.create();
        for(Phrase phrase : part.getPhraseArray()){
            Note[] notes = phrase.getNoteArray();
            if(notes.length == 0)
                continue;
            double currentPhraseLength = phrase.getStartTime();
            for(Note note : notes){
                int notePitch = note.getPitch();
                if(notePitch == Note.REST)
                    continue;
                currentPhraseLength += note.getRhythmValue();
                notesPitches.put(currentPhraseLength, notePitch);
            }
        }     
        
        /**Build chord patterns as a string to be able to find and remove patterns later
         * And for every key in the ListMultimap get pitch list and
         * create and save a Chord object to find patterns for*/
        String valuesAsString = "";
        List<Chord> chordArray = new ArrayList<Chord>();
        Set<Double> keys = notesPitches.keySet();
        for(Double d : keys){
            List<Integer> pitches = notesPitches.get(d);
            for(Integer i : pitches){
                valuesAsString = valuesAsString + i + " ";
            }
            valuesAsString = valuesAsString + " ";
            chordArray.add(new Chord(pitches));
        }    
        
        /**Save length of chord values as string before removal of patterns*/
        double before = (double)valuesAsString.replaceAll("\\s+","").length();
        /**Minimum length of pattern is 1% of values as string length*/
        int minimum = (int)(before*0.01);
        
        /**Find every Chord pattern longer than minimum*/
        List<List<Chord>> foundChordPatterns = findChordPatterns(chordArray, minimum);
        
        /**For every found chord pattern save it as string in map where key is 
         * number of occurrences in chord values as string * length of pattern
         * to be able to remove as much of the patterns from chord values as possible*/
        ListMultimap<Integer, String> occurrences = ArrayListMultimap.create();
        for(List<Chord> cList : foundChordPatterns){
            String nextChordPattern = "";
            for(Chord c : cList){
                nextChordPattern = nextChordPattern + c.toString();
                nextChordPattern = nextChordPattern + " ";
            }
            /**Save next pattern as string with key as number of occurrences * length of pattern */
            Integer occurrence = (this.numberOfOccurrences(nextChordPattern, valuesAsString) * nextChordPattern.length());
            occurrences.put(occurrence, nextChordPattern);
            
            /**Sort the keys in decending order to retrive the patterns who is removing largest
             * amount of % from chord as string value first*/
            ArrayList<Integer> intKeys = new ArrayList<Integer>(occurrences.keySet());
            Collections.sort(intKeys, Collections.reverseOrder());
            for(Integer k : intKeys){
                List<String> p = occurrences.get(k);
                for(String sC : p){
                    if(this.numberOfOccurrences(sC, valuesAsString) >= 2)
                        valuesAsString = valuesAsString.replace(sC, "");
                }
            }
            
            
                
        }        
        /**Save length of string after removal of every pattern found*/
        double after = (double)valuesAsString.replaceAll("\\s+","").length();
        
        return 1 - (after/before);
    }

    
    public List<List<Chord>> findChordPatterns(List<Chord> chords, int min){
        
        /**Best result and suffix lists*/
        List<List<Chord>> bestResults = new ArrayList<List<Chord>>();
        List<List<Chord>> suffixList = new ArrayList<List<Chord>>();
        
        /**Save lenght of input list*/
        int inputLength = chords.size();
        
        /**Build suffix lists from chord input list*/
        for(int i = 0; i < inputLength; i++){
            List<Chord> nextSuffix = chords.subList(i, inputLength);
            if(nextSuffix.size() >= min)
                suffixList.add(nextSuffix);
        }
        
        /**Save lenght of list of suffix lists*/
        inputLength = suffixList.size();
        
        
        /**Sort the list of suffix list by the chord values in the suffix lists*/
        this.sortByValues(suffixList);
        
        List<Chord> nextFound = new ArrayList<Chord>();    
        
        /**Declare variables to keep track of 
         * difference between current list being compared (distance) and
         * how far ahead to check to remove overlaps (checkFurter)
         * */
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
                
                /** if next suffixes don't at least match or is as long as the min value,
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
    
    /**Private inner class to represent a list of pitch values as a Chord*/
    private class Chord{
        List<Integer> notes;
        
        public Chord(List<Integer> notes){
            this.notes = notes;
        }
        
        /**Method compares this chord object with another
         * by first comparing pitch values, returns 1 if any 
         * pitch value in this chord is larger, otherwise -1
         * if every value is the same it returns 1 if this chord object 
         * has more pitch values otherwise -1 if other chord has more, and 
         * eventually it return 0 if both chord objects is considered equal*/
        public int lessThan(Chord chord){
            int minSize =  Math.min(this.notes.size(), chord.notes.size());
            for(int i = 0; i < minSize; i++){
                Integer first = chord.notes.get(i);
                Integer second = this.notes.get(i);
                if(first < second)
                    return 1;
                else if(first > second)
                    return -1;
            }
            if(chord.notes.size() < this.notes.size())
                return 1;
            else if(chord.notes.size() > this.notes.size())
                return -1;
            return 0;
        }
        
        /**equals method checks every pitch value to 
         * determine if they are the same*/
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
    
    /**
     * Sorts the List of sublists containing Chord Objects by
     * comparing the sublists Chord objects and  
     * sorts the order of the sublists accordingly
     * @param list List of sublists containing Chord objects to be sorted
     */
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
    
    
    /** Finds longest common sequence for two lists containing Double values
     * @param first First list of Chord objects to compare with
     * @param second Second list of Chord objects to compare with
     * @param max inte value representing longest allowed pattern to avoid overlapping
     * @return List of Chord objects representing longest common sequence for params 
     * */
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
    
    
    /**
     * Sort list of sublists containing Chord objects
     * by comparing the size of the sublists and sorts them
     * from largest to smallest
     * @param list List of sublists contaning Chord objects to be sorted
     * */
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
     * @return int Number of occurrences of pattern in content 
     * */
    private int numberOfOccurrences(String pattern, String content){
        return content.split(Pattern.quote(pattern), -1).length - 1;
    }
    
}
