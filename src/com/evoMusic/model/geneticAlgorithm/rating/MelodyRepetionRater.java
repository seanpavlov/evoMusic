package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

public class MelodyRepetionRater extends SubRater {
    
    /**
     * Constructor, creates a user rater
     * 
     * @param targetRating
     */
    public MelodyRepetionRater(double targetRating){
        super.setTargetRating(targetRating);
    }
    
    @Override
    public double rate(Song song) {
        double rating = 0; 
        List<Track> tracks = new ArrayList<Track>();
        tracks.addAll(song.getTaggedTracks(TrackTag.MELODY));

        if (tracks.isEmpty()) {
            return 0.0; 
        }
        int i = 0;
        for (Track t : tracks){
            i++;
            rating += ratePart(t.getPart());
        }
        
        Double rate = rating / i;
        return rate;
    }
    
    /**
     * Rates a part 
     * 
     * @param part Part to be rated
     * @return Integer Rating value for the rated part
     * **/
    private Double ratePart(Part part){
        /**Phrase array and variables 
         * for ratings and number of phrases*/
        Phrase[]        phrases = part.getPhraseArray();
        List<Note>      allNotes = new ArrayList<Note>();
        List<Integer>   values = new ArrayList<Integer>();
        String valuesAsString = "";

        Double rating = 0.0;
        Integer numberOfPhrases = phrases.length;
        if (numberOfPhrases < 1){ 
            return 0.0;
        }
        
        for(Phrase phrase : phrases){
            for(Note n : phrase.getNoteArray()){
                if (!n.isRest()){
                    allNotes.add(n);
                    values.add(n.getPitch() % 12);
                    valuesAsString += n.getPitch() % 12;
                }
            }
        }
        
        Integer accuracity = 10;
        Integer before = valuesAsString.length();
        if (before < 1){
            return 0.0;  
        }else if (before < accuracity){
            accuracity = before;
        }
        
        Integer minimum = (int) Math.ceil(allNotes.size() / accuracity);
        
        /**Find repeating patters of min length*/
        List<List<Integer>> longest = this.findPatterns(values, minimum);
                
        /**Remove occurrence of patterns in valuesAsString variable*/
        for(List<Integer> dList : longest){
            String nextPattern = "";
            for(Integer d : dList){
                nextPattern += d;
            }

            /**
             * Check if pattern occurre more than once
             * in remaining valueAsString variable, pattern
             *  might been a part of larger pattern already removed
             * */
            if(this.numberOfOccurrences(nextPattern, valuesAsString) >= 2)
            valuesAsString = valuesAsString.replace(nextPattern, "");
        }
        
        /**Get string length after removal of found patterns*/
        Integer after = (Integer)valuesAsString.length(); 

        Double ab = (double) after/before;
        
        /**Calculates rate by checking list size before removal of pattern found and after*/
        rating += 1.0 - ab;

        /**Calculate total value by calculating median of all phrases*/
        return rating/numberOfPhrases;
    }
    
    /**
     * Get Longest repeating sequences as list of lists of Integer values  
     * 
     * @param values List of Integer values to find repeating pattern in
     * @return List<List<Integer>> List of Lists of Integer values representing
     *                               longest repeating patterns in param value
     * */
   public List<List<Integer>> findPatterns(List<Integer> values, int min){
        
        /**Best result and suffix lists*/
        List<List<Integer>> bestResults = new ArrayList<List<Integer>>();
        List<List<Integer>> suffixLists = new ArrayList<List<Integer>>();
        /**Save number of input values, bool and Integer variables to keep track if 
         * all values are the same*/
        int inputLength = values.size();
        boolean allTheSame = true;
        Integer lastValue = null;
               
        for(int i = 0 ; i < inputLength; i++){
            /**Keep track if every value is exactly the same*/
            if(lastValue == null || lastValue.equals(values.get(i)))
                lastValue = values.get(i);
            else
                allTheSame = false;
            
            /**Add next suffix list*/
            suffixLists.add(values.subList(i, inputLength));
        }
   
        /**If all values are the same, the largest
         *(and only) pattern is half the input list 
         */
        if(allTheSame){
            bestResults.add(values.subList(0, inputLength/2));
            return bestResults;
        }
        
        /**
         * Sorts the List of sublists containing Integer values by
         * comparing the sublists elements and  
         * sorts the order of the sublists accordingly
         */
        this.sortByValues(suffixLists);
        
        /**Declare nextFound arraylist to store next found pattern*/
        List<Integer> nextFound= new ArrayList<Integer>();
        
        /**Declare variables to keep track of largest found so far (atLeast),
         * difference between current list being compared (distance) and
         * how far ahead to check to remove overlaps (checkFurter)
         * */
        //int atLeast = 1;
        int distance;
        int checkFurter = 1;
            
        for(int i = 1; i < inputLength; i++){
            List<Integer> firstList = suffixLists.get(i);
            for(int n = checkFurter; n >= 1; n--){
                
                List<Integer> secondList = (i >= checkFurter) ? 
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
                bestResults.add(0, nextFound);
                //atLeast = nextFound.size() + 1;
                
                checkFurter = (nextFound.size() == distance) ?
                        Math.max(checkFurter, n+1) : n;                
            }
            
        }
        this.sortBySize(bestResults);
        return bestResults;
    }
    
    /** Finds longest common sequence for two lists containing Integer values
     * @param first First list of Integer values to compare with
     * @param second Second list of Integer values to compare with
     * @param max inte value representing longest allowed pattern to avoid overlapping
     * @return List of Integer values representing longest common sequence for params 
     * */
   private List<Integer> longestCommon(List<Integer> first, List<Integer> second, int max){
       int n = Math.min(first.size(), second.size());
       if(max < n){
           n = max;
       }
       List<Integer> longestCommon = new ArrayList<Integer>();
       for(int i = 0; i < n; i++){
           if(!first.get(i).equals(second.get(i))){
               return longestCommon;
           }
           longestCommon.add(first.get(i));
       }
       return longestCommon;
   }
   
   
   /**
    * Sorts the List of sublists containing Integer values by
    * comparing the sublists elements and  
    * sorts the order of the sublists accordingly
    * @param suffixLists List of sublists containing Integer values to be sorted
    */
   private void sortByValues(List<List<Integer>> list){
       Collections.sort(list, new Comparator<List<Integer>>() {
           public int compare(List<Integer> values, List<Integer> otherValues) {
               int minSize =  Math.min(values.size(), otherValues.size());
               for(int i = 0; i < minSize; i++){
                   int first = values.get(i);
                   int second = otherValues.get(i);
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
    * Sort list of sublists containing Integer values
    * by comparing the size of the sublists and sorts them
    * from largest to smallest
    * @param list List of sublists contaning Integer values to be sorted
    * */
   private void sortBySize(List<List<Integer>> list){
       Collections.sort(list, new Comparator<List<Integer>>(){
           public int compare(List<Integer> values, List<Integer> otherValues){
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
       return content.split(pattern).length - 1;
   }
}