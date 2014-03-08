package com.evoMusic.model.geneticAlgorithm.rating;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jm.music.data.Part;

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
        return rating/count;
    }
    
    /**
     * Rates a part 
     * @param part Part to be rated
     * @return double Rating value for the rated part
     * **/
    private double ratePart(Part part){
        List<Double> rythmList = new ArrayList<Double>();
        //Get the rythm array for the first phrase
        double[] rythmArray = part.getPhraseArray()[0].getRhythmArray();
        String asString = "";
        //Add values to list and build values as string
        for(double d : rythmArray){
            asString = asString + d;
            rythmList.add(d);
        }
        
        //Calculate longest repeating pattern in list
        String longestAsString = "";
        List<Double> longest = this.longestSequence(rythmList);
        for(Double d : longest){
            longestAsString = longestAsString + d;
        }
        int before = asString.length();
        asString  = asString.replace(longestAsString, "");
        int after = asString.length();
        
        //Calculates rate by checking list size before removal of pattern found and after
        return 1.0-((double)after/(double)before);
    }
    
    
    /**
     * Get Longest repeating sequence in list of doubles
     * 
     * @param rythmList List of Double values to find repeating pattern in
     * @return List<Double> Double values representing longest repeating pattern in param 
     * */
   public List<Double> longestSequence(List<Double> rythmList){
        
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
        if(allTheSame)
            return rythmList.subList(0, stringLength/2);
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
        
        
        List<Double> bestFound= new ArrayList<Double>();
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
                
                bestFound = this.longestCommon(list1, list2, distance);
                atLeast = bestFound.size()+1;
                if(bestFound.size() == distance){
                    checkFurter = Math.max(checkFurter, n+1);
                }else{
                    checkFurter = n;
                }
                
            }
            
        }
        
        return bestFound;
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
