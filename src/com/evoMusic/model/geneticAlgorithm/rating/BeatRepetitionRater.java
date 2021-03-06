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
import com.evoMusic.parameters.P;
import com.evoMusic.util.TrackTag;

public class BeatRepetitionRater extends SubRater {  
 
    public BeatRepetitionRater(double targetRating) {
        super.setTargetRating(targetRating);
        super.setInfluenceMultiplier(P.RATER_BEAT_REPETITION_INFLUENCE_MUL);
    }
    
    /**
     * Rates song by analyzing beat/rythm
     * @param song Song to be rated
     * @return double Value of rating for song
     * */
    public double rate(Song song) {
        /**
         * Rates every part in song that is tagged with RYTHM
         * and returns mean value for every rated part
         */
        double rating = 0;
        double count = 0;
        List<Track> targetTracks = song.getTaggedTracks(TrackTag.BEAT);
        targetTracks.addAll(song.getTaggedTracks(TrackTag.RHYTHM));
        
        if(targetTracks.size() == 0)
            return 0.0;
        
        for(Track track : targetTracks){
            rating += this.ratePart(track.getPart());
            ++count;
        }
        return rating/count;
    }
    
    /**
     * Rates a part 
     * @param part Part to be rated
     * @return double Rating value for the rated part
     * **/
    private double ratePart(Part part){
        /**Phrase array and variables 
         * for ratings and number of phrases*/
        Phrase[] phrases = part.getPhraseArray();
        double rating = 0;
        double numberOfPhrases = phrases.length;
        for(Phrase phrase : phrases){
            List<Double> values = new ArrayList<Double>();
            String valuesAsString = "";
            /**Add rhythm values to list and build valuesAsString variable*/        
            for(Note n : phrase.getNoteArray()){              
                if(!n.isRest()){
                    double rhythmValue = n.getRhythmValue();
                    valuesAsString = valuesAsString + rhythmValue;
                    values.add(rhythmValue);
                }
            }
            
            /**Save length before removal of patterns found*/
            double before = (double)valuesAsString.length();
            double minimum = phrase.getRhythmArray().length * 0.1;

            /**Find repeating patters of min length*/
            List<List<Double>> longest = this.findPatterns(values, (int)minimum);

            Map<Integer, List<String>> occurrence = new HashMap<Integer, List<String>>();
            /**Remove occurrence of patterns in valuesAsString variable*/
            for(List<Double> dList : longest){
                String nextPattern = "";
                for(Double d : dList){
                    nextPattern = nextPattern + d;
                }
                /**Map string to how many occurences * length of pattern*/
                Integer s = (this.numberOfOccurrences(nextPattern, valuesAsString)* nextPattern.length());
                List<String> strings = occurrence.get(s);
                if(strings == null){
                    strings = new ArrayList<String>();
                    strings.add(nextPattern);
                    occurrence.put(s, strings);
                }else{
                    strings.add(nextPattern);
                    occurrence.put(s, strings);
                }
                
            } 
            ArrayList<Integer> intKeys = new ArrayList<Integer>(occurrence.keySet());
            Collections.sort(intKeys, Collections.reverseOrder());
            for(Integer k : intKeys){
                List<String> p = occurrence.get(k);
                for(String s : p){
                    /**
                     * Check if pattern occurre more than once
                     * in remaining valueAsString variable, pattern
                     *  might been a part of larger pattern already removed
                     * */
                    if(this.numberOfOccurrences(s, valuesAsString) >= 2)
                        valuesAsString = valuesAsString.replace(s, "");
                }
            }       
            /**Get string length after removal of found patterns*/
            double after = (double)valuesAsString.length();
            /**Calculates rate by checking list size before removal of pattern found and after*/
            rating += 1.0 - (after/before);
        }
      
        /**Calculate total value by calculating median of all phrases*/
        return rating/numberOfPhrases;
    }
    
    
    /**
     * Get Longest repeating sequences as list of lists of double values  
     * 
     * @param values List of Double values to find repeating pattern in
     * @return List<List<Double>> List of Lists of Double values representing
     *                               longest repeating patterns in param value
     * */
   public List<List<Double>> findPatterns(List<Double> values, int min){
        
        /**Best result and suffix lists*/
        List<List<Double>> bestResults = new ArrayList<List<Double>>();
        List<List<Double>> suffixLists = new ArrayList<List<Double>>();
        /**Save number of input values, bool and Double variables to keep track if 
         * all values are the same*/
        int inputLength = values.size();
        boolean allTheSame = true;
        Double lastValue = null;
               
        for(int i = 0 ; i < inputLength; i++){
            /**Keep track if every value is exactly the same*/
            if(lastValue == null || lastValue.equals(values.get(i)))
                lastValue = values.get(i);
            else
                allTheSame = false;
            
            /**Add next suffix list*/
            List<Double> nextSuffix = values.subList(i, inputLength);
            if(nextSuffix.size() >= min)
                suffixLists.add(values.subList(i, inputLength));
        }
   
        /**If all values are the same, the largest
         *(and only) pattern is half the input list 
         */
        if(allTheSame){
            bestResults.add(values.subList(0, inputLength/2));
            return bestResults;
        }
        
        inputLength = suffixLists.size();
        
        /**
         * Sorts the List of sublists containing Double values by
         * comparing the sublists elements and  
         * sorts the order of the sublists accordingly
         */
        this.sortByValues(suffixLists);
        
        /**Declare nextFound arraylist to store next found pattern*/
        List<Double> nextFound= new ArrayList<Double>();
        
        /**Declare variables to keep track of largest found so far (atLeast),
         * difference between current list being compared (distance) and
         * how far ahead to check to remove overlaps (checkFurter)
         * */
        int distance;
        int checkFurter = 1;
        for(int i = 1; i < inputLength; i++){
            List<Double> firstList = suffixLists.get(i);
            for(int n = checkFurter; n >= 1; n--){
                
                List<Double> secondList = (i >= checkFurter) ? 
                        suffixLists.get(i-checkFurter) : suffixLists.get(i);
                
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
                if (!bestResults.contains(nextFound)){
                    bestResults.add(0, nextFound);
                    checkFurter = (nextFound.size() == distance) ?
                        Math.max(checkFurter, n+1) : n;                
                }
            }
        }
        this.sortBySize(bestResults);
        return bestResults;
    }
    
    /** Finds longest common sequence for two lists containing Double values
     * @param first First list of Double values to compare with
     * @param second Second list of Double values to compare with
     * @param max inte value representing longest allowed pattern to avoid overlapping
     * @return List of Double values representing longest common sequence for params 
     * */
   private List<Double> longestCommon(List<Double> first, List<Double> second, int max){
       int n = Math.min(first.size(), second.size());
       if(max < n){
           n = max;
       }
       List<Double> longestCommon = new ArrayList<Double>();
       for(int i = 0; i < n; i++){
           if(!first.get(i).equals(second.get(i))){
               return longestCommon;
           }
           longestCommon.add(first.get(i));
       }
       return longestCommon;
   }
   
   
   /**
    * Sorts the List of sublists containing Double values by
    * comparing the sublists elements and  
    * sorts the order of the sublists accordingly
    * @param list List of sublists containing Double values to be sorted
    */
   public void sortByValues(List<List<Double>> list){
       Collections.sort(list,new Comparator<List<Double>>() {
           public int compare(List<Double> values, List<Double> otherValues) {
               int minSize =  Math.min(values.size(), otherValues.size());
               for(int i = 0; i < minSize; i++){
                   double first = values.get(i);
                   double second = otherValues.get(i);
                   if(first < second)
                       return -1;
                   else if(first > second)
                       return 1;
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
   
   /**
    * Sort list of sublists containing Double values
    * by comparing the size of the sublists and sorts them
    * from largest to smallest
    * @param list List of sublists contaning Double values to be sorted
    * */
   private void sortBySize(List<List<Double>> list){
       Collections.sort(list, new Comparator<List<Double>>(){
           public int compare(List<Double> values, List<Double> otherValues){
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
