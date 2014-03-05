package com.evoMusic.model.geneticAlgorithm.rating;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jm.music.data.Part;

import com.evoMusic.model.Song;

public class BeatRater extends SubRater{  
 
    
    /**
     * Rates song by analyzing beat/rythm
     * @param song Song to be rated
     * @return double Value of rating for song
     * */
    public double rate(Song song) {
        //Not implemented correctly, just during development
        System.out.println(song.getScore().getPartArray().length);   
        return this.ratePart(song.getScore().getPart(1));
    }
    
    /**
     * Rates a part 
     * @param part Part to be rated
     * @return double Rating value for the rated part
     * **/
    private double ratePart(Part part){
        List<Double> rythmList = new ArrayList<Double>();
        //Add values to list
        double[] rythmArray = part.getPhraseArray()[0].getRhythmArray();
        for(double d : rythmArray){
            rythmList.add(d);
        }
        
        //Calculate longest repeating pattern in list
        List<Double> longestSequence = this.longestSequence(rythmList);
        
        //Save list size for later 
        int rythmListSize = rythmList.size();
        //Bool to keep track if pointer has been set
        boolean start = false;
        //Pointer to keep track where pattern in list started
        int pointer = 0;
        //Local pointer to check patter agains list
        int localPointer = 0;
        //Finds and removes longest patterns in list
        for(int i = 0; i < rythmList.size(); i++){
            Double localValueList = rythmList.get(i);
            Double localValueCommon = longestSequence.get(localPointer);
            if(localValueList.equals(localValueCommon)){
                if(!start){
                    pointer = i;
                    ++localPointer;
                    start = true;
                }else if(!((i-pointer)+1 < longestSequence.size())){
                    for(int k = pointer; k <= i; k++){
                        rythmList.remove(k);
                    }
                    pointer = 0;
                    localPointer = 0;
                    start = false;
                }else{
                    ++localPointer;
                }
            }else{
                pointer = 0;
                localPointer = 0;
                start = false;
            }
        }
        
        //Calculates rate by checking list size before removal of pattern found and after
        return 1.0-((double)rythmList.size()/(double)rythmListSize);
    }
    
    
    
    /**NOT DONE! Gives weird results for larg collections**/
    /**
     * Get Longest repeating sequence in list of doubles
     * 
     * @param rythmList List of Double values to find repeating pattern in
     * @return List of Double values representing longest repeating pattern in param 
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
       
        /*Find and return longest common list by
         * comparing every sublist
         */
        List<Double> longestRepeat = new ArrayList<Double>();
        for(int i = 0; i < stringLength - 1 ; i++){
           List<Double> localLongestRepeat = this.longestCommon(subStrings.get(i), subStrings.get(i+1));
           if(localLongestRepeat.size() > longestRepeat.size()){
               longestRepeat = localLongestRepeat;
           }
            
        }
        return longestRepeat;
    }
    

    
    /**TODO Needs work to get better results
     * Finds longest common sequence for two lists of double values
     * @param first First list of Double values to compare with
     * @param second Second list of Double values to compare with
     * @return List of Double values representing longest common sequence for params
     * 
     * */
    private List<Double> longestCommon(List<Double> first, List<Double> second){
        int n = Math.min(first.size(), second.size());
        int m = Math.max(first.size(), second.size());
        //Trying differenct allowed values to get best result
        if(m-n <= 50)
            return new ArrayList<Double>();
        
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
