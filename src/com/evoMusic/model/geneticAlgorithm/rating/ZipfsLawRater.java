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
        
        for (List<Note> notes : sortedNoteList){
            for (Note n : notes){
                int pitchModOctave = n.getPitch() % 12;
                if (pitchOccurances.containsKey(pitchModOctave)){
                    pitchOccurances.put(pitchModOctave, pitchOccurances.get(pitchModOctave)+1);
                } else {
                    pitchOccurances.put(pitchModOctave, 1);
                }
            }
        }
        
        Collection<Integer> values = pitchOccurances.values();
        List<Integer> valuesAsList = new ArrayList<Integer>(values);
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
        List<Integer> perfValues = new ArrayList<Integer>();
        Integer perfTot = 0;
        Integer tempTot = 0;
        List<Integer> temp = new ArrayList<Integer>();
        List<Integer> perf = new ArrayList<Integer>();
        
        for (int x = 0; x < values.size(); x++){
            perfValues.add(values.get(0) / (x+1));
        }
        
        for (int x = 0; x < values.size(); x++){
            Integer v = (x+1) * values.get(x);
            Integer vPerf = (x+1) * perfValues.get(x);
            temp.add(v);
            perf.add(vPerf);
            tempTot += v;
            perfTot += vPerf;
        }
        
        tempTot = tempTot / values.size();
        perfTot = perfTot / perfValues.size();
        return (double) Math.min(tempTot, perfTot) / (double)Math.max(tempTot, perfTot);
    }

}
