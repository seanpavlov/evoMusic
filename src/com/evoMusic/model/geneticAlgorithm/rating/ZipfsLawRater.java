package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.*;

import jm.music.data.Note;
import com.evoMusic.model.Song;
import com.evoMusic.util.Sort;

public class ZipfsLawRater extends SubRater {

    /**
     * Constructor for ZipfsLawRater
     * @param weight
     */
    public ZipfsLawRater(double weight){
        super.setWeight(weight);
    }
    
    
    @Override
    public double rate(Song song) {
        List<List<Note>> sortedNoteList = Sort.getSortedNoteList(song);
        Map<Integer, Integer> pitchOccurances = new HashMap<Integer, Integer>(); 
        
        /**Sort note pitches by which octave they belong to*/
        for (List<Note> notes : sortedNoteList){
            for (Note n : notes){
                int pitchModOctave = n.getPitch()% 12;
                if (pitchOccurances.containsKey(pitchModOctave)){
                    pitchOccurances.put(pitchModOctave, pitchOccurances.get(pitchModOctave)+1);
                } else {
                    pitchOccurances.put(pitchModOctave, 1);
                }
            }
        }
        /**Sort pitch occurences in descending order*/
        List<Integer> valuesAsList = new ArrayList<Integer>(pitchOccurances.values());
        Collections.sort(valuesAsList, Collections.reverseOrder());
        return calcMedianDiffs(valuesAsList);
    }

    /**
     * Calculates the differences in medians between the optimal and our own
     * Zipf's values.
     * 
     * @param values
     * @return 
     */
    private double calcMedianDiffs(List<Integer> values){
        /**Zipf values calculated from highest ranking pitch*/
        List<Integer> perfectZipfValues = new ArrayList<Integer>();
        /**Variables to hold total of all Zipf values*/
        double perfectZipfTotal = 0;
        double zipfTotal = 0;
        /**List to hold calculated C variable from Zipf's law*/
        List<Integer> zipfVariableValues = new ArrayList<Integer>();
        List<Integer> zipfPerfectVariableValues = new ArrayList<Integer>();
        
        /*Calculate perfect zipf values from highest ranking value**/
        for (int x = 0; x < values.size(); x++){
            perfectZipfValues.add(values.get(0) / (x+1));
        }
        
        /**Calculate C variable from Zipf's law and total amount*/
        for (int x = 0; x < values.size(); x++){
            Integer v = (x+1) * values.get(x);
            Integer vPerf = (x+1) * perfectZipfValues.get(x);
            zipfVariableValues.add(v);
            zipfPerfectVariableValues.add(vPerf);
            zipfTotal += v;
            perfectZipfTotal += vPerf;
        }
        /**Calculate median for perfect and current zipf values*/
        double medianZipf = zipfTotal / values.size();
        double medianPerfectzipf = perfectZipfTotal / perfectZipfValues.size();
        return Math.min(medianZipf, medianPerfectzipf) / Math.max(medianZipf, medianPerfectzipf);
    }

}
