package com.evoMusic.model.geneticAlgorithm.rating;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.enumerators.TrackTag;

public class BeatRater extends SubRater{  
 
    
    /**
     * Rates song by analyzing beat/rythm
     * @param song Song to be rated
     * @return double Value of rating for song
     * */
    public double rate(Song song) {
        //Rates every part in song that is tagged with RYTHM
        //and returns mean value for every rated part
        double rating = 0;
        int count = 0;
        for(Part part : song.getScore().getPartArray()){
            List<TrackTag> trackTags = song.getTrackTags(part);
            if(trackTags.contains(TrackTag.RHYTHM) ||
                    trackTags.contains(TrackTag.BEAT)){
                ++count;
                rating += this.ratePart(part);
            }
        }
        if(count == 0)
            return 0.0;
        return rating/(double)count;
    }
    
    /**
     * Rates a part 
     * @param part Part to be rated
     * @return double Rating value for the rated part
     * **/
    private double ratePart(Part part){
        
        //Get the rythm array for the first phrase
        int count = 0;
        double rating = 0;
        for(Phrase phrase : part.getPhraseArray()){
            List<Double> rythmList = new ArrayList<Double>();
            String asString = "";
            //Add values to list and build values as string
            for(double d : phrase.getRhythmArray()){
                asString = asString + d;
                rythmList.add(d);
            }
            
            //Save length before removal of pattern found
            int before = asString.length();
            //Calculate longest repeating patterns in list
            List<List<Double>> longest = this.longestSequence(rythmList);
            for(List<Double> dList : longest){
                String nextPattern = "";
                for(Double d : dList){
                    nextPattern = nextPattern + d;
                }
                asString = asString.replace(nextPattern, "");
            }        
            //Get string length after removal of found pattern
            int after = asString.length(); 
            //Calculates rate by checking list size before removal of pattern found and after
            rating += 1.0 - ((double)after/(double)before);
        }
        
        
        //Calculate total value by calculating median of all phrases
        return rating/(double)count;
    }
    
    
    /**
     * Get Longest repeating sequences as lists of double list 
     * 
     * @param rythmList List of Double values to find repeating pattern in
     * @return List<List<Double>> List of Lists of Double values representing
     *                               longest repeating pattern in param 
     * */
   public List<List<Double>> longestSequence(List<Double> rythmList){
        
        List<List<Double>> bestResult = new ArrayList<List<Double>>();
        //Create sublists from the input list
        int stringLength = rythmList.size();
        List<List<Double>> subStrings = new ArrayList<List<Double>>();
        boolean allTheSame = true;
        Double lastValue = null;
        //System.out.println("Values: ");
        for(int i = 0 ; i < stringLength; i++){
            //Keep track if every value is exactly the same
            if(lastValue == null)
                lastValue = rythmList.get(i);
            else if(lastValue.equals(rythmList.get(i)))
                lastValue = rythmList.get(i);
            else
                allTheSame = false;
               
            subStrings.add(rythmList.subList(i, stringLength));
        }
   
        //If list of values is all the same, largest pattern is half the list
        if(allTheSame){
            bestResult.add(rythmList.subList(0, stringLength/2));
            return bestResult;
        }
        /*
         * Sorts the List of sublists containing Double values by
         * comparing the sublists elements and  
         * sorting order of the sublists accordingly
         */
        Collections.sort(subStrings,new Comparator<List<Double>>() {
            public int compare(List<Double> values, List<Double> otherValues) {
                int maxValue =  Math.max(values.size(), otherValues.size());
                String firstArray = "";
                String secondArray = "";
                for(int i = 0; i < maxValue; i++){
                    if(i<values.size())
                        firstArray = firstArray + values.get(i);
                    if(i<otherValues.size())
                        secondArray = secondArray + otherValues.get(i);
                }
                return firstArray.compareTo(secondArray);
            }
        });
        
        
        List<Double> nextFound= new ArrayList<Double>();
        int atLeast = 1;
        int distance;
        int checkFurter = 1;
        
        for(int i = 1; i < stringLength; i++){
            List<Double> list1 = subStrings.get(i);
            for(int n = checkFurter; n >= 1; n--){
                List<Double> list2;
                if(i >= checkFurter)
                    list2 = subStrings.get(i-checkFurter);
                else
                    list2 = subStrings.get(i);
                distance = Math.abs(list1.size() - list2.size());
                
                if(distance < atLeast){
                    if(list1.size() >= atLeast &&
                       list2.size() >= atLeast &&
                       list1.subList(0, atLeast).equals(list2.subList(0, atLeast))){
                        checkFurter = Math.max(checkFurter, n+1);
                    }else{
                        checkFurter = n;
                    }
                    continue;
                }
                
                /*if next suffixes don't at least match or is as long as the best,
                    no need to check more carefully
                */
                if(!(list1.size() >= atLeast) || 
                   !(list2.size() >= atLeast) ||
                   !list1.subList(0, atLeast).equals(list2.subList(0, atLeast))){
                    checkFurter = n;
                    continue;
                }
                
                nextFound = this.longestCommon(list1, list2, distance);
                bestResult.add(0, nextFound);
                atLeast = nextFound.size()+1;
                if(nextFound.size() == distance){
                    checkFurter = Math.max(checkFurter, n+1);
                }else{
                    checkFurter = n;
                }
                
            }
            
        }
        
        return bestResult;
    }
    
    /* Finds longest common sequence for two lists of double values
     * @param first First list of Double values to compare with
     * @param second Second list of Double values to compare with
     * @param max inte value representing longest allowed pattern to avoid overlapping
     * @return List of Double values representing longest common sequence for params
     * 
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
}
