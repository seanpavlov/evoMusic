package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Map<Double, Chord> chords = new HashMap<Double, Chord>();
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
                
                Chord chord = chords.get(currentPhraseLength);
                if(chord != null){
                    chord.addNote(notePitch);
                    chords.put(currentPhraseLength, chord);
                }else{
                    chord = new Chord();
                    chord.addNote(notePitch);
                    chords.put(currentPhraseLength, chord);
                }
            }
        }    
        
        /**Build chord patterns as a string to be able to find and remove patterns later
         * And add every chord into a chord list*/
        String valuesAsString = "";
        List<Chord> chordArray = new ArrayList<Chord>();
        for(Map.Entry<Double ,Chord> entry : chords.entrySet()) {
            Chord chord = entry.getValue();
            valuesAsString += chord.chordString + " ";
            chordArray.add(chord);
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
                nextChordPattern = nextChordPattern + c.toString() + " ";
            }
            /**Save next pattern as string with key as number of occurrences * length of pattern */
            Integer occurrence = (this.numberOfOccurrences(nextChordPattern, valuesAsString) * nextChordPattern.length());
            occurrences.put(occurrence, nextChordPattern);
            
            /**Sort the keys in decending order to retrive the patterns who is removing largest
             * amount of % from chord as string value first*/
            ArrayList<Integer> intKeys = new ArrayList<Integer>(occurrences.keySet());
            Collections.sort(intKeys, Collections.reverseOrder());
            for(Integer key : intKeys){
                List<String> patterns = occurrences.get(key);
                for(String pattern : patterns){
                    if(this.numberOfOccurrences(pattern, valuesAsString) >= 2)
                        valuesAsString = valuesAsString.replace(pattern, "");
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
    
    /**Private inner class to represent a Chord with list of 
     * pitch values and string containing these pitch values*/
    private class Chord implements Comparable<Chord>{
        List<Integer> notes;
        String chordString;
        
        public Chord(){
            notes = new ArrayList<Integer>();
            chordString = "";
        }
        
        public void addNote(Integer notePitch){
            notes.add(notePitch);
            chordString += notePitch + " ";
        }
        
        @Override 
        public boolean equals(Object other){
            if(!(other instanceof Chord)){
                return false;
            }
            Chord otherChord = (Chord)other;
            return this.notes.equals(otherChord.notes);
        }
        
        @Override
        public String toString(){
            String toString = "";
            for(Integer pitch : this.notes){
                toString = toString + pitch + " ";
            }
            return toString;
        }

        @Override
        public int compareTo(Chord other) {
            int length = this.notes.size();
            if(length == other.notes.size()){
                for(int i = 0; i < length; i++){
                    Integer pitch = this.notes.get(i);
                    Integer otherPitch = other.notes.get(i);
                    if(pitch > otherPitch){
                        return 1;
                    }else if(pitch < otherPitch){
                        return -1;
                    }
                }
                return 0;
            }else if(length > other.notes.size()){
                return 1;
            }else{
                return -1;
            }
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
                int valueLength = values.size();
                int otherValueLenght = otherValues.size();               
                int minSize =  Math.min(valueLength, otherValueLenght);
                for(int i = 0; i < minSize; i++){
                    Chord value = values.get(i);
                    Chord otherValue = otherValues.get(i);
                    int compare = value.compareTo(otherValue);
                    if(compare != 0){
                        return compare;
                    }
                }
                if(valueLength < otherValueLenght){
                    return -1;
                }else if(valueLength > otherValueLenght){
                    return 1;
                }
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
