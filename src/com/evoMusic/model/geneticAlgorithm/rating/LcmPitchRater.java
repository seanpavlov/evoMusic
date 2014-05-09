package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.List;
import jm.music.data.Note;
import com.evoMusic.model.Song;
import com.evoMusic.util.Sort;

public class LcmPitchRater extends SubRater{
    /**
     * Constructor for LcmPitchRater
     * 
     * @param weight
     */
    public LcmPitchRater(double weight){
        super.setWeight(weight);
    }
    
    /**
     * Rates the dissonance level of a song, the more dissonance the notes are to each
     * other the worse rating. Dissonance are calculated with the help of a formula given by 
     * Euler. Notes that are alone at one single point is has dissonance 0.
     */
    @Override
    public double rate(Song song) {
        List<List<Note>> sortedNotes = Sort.getSortedNoteList(song);
        double nbrOfNotes = 0;
        double disonance = 1.0;
        
        for(List<Note> notes : sortedNotes){
            List<Integer> frequencies= new ArrayList<Integer>();
            for(Note note : notes){
                if(note.getPitch() != Note.REST){
                    int frequency =  note.getPitch() % 12 + 1;
                    frequencies.add(frequency);                   
                }
            }
            
            nbrOfNotes++;
            if (frequencies.size() > 1){
                int gcd = gcdMultiple(frequencies);
                List<Integer> ratios = new ArrayList<Integer>();
                for(Integer d : frequencies){
                    ratios.add(d/gcd);
                }
                int lcm = lcmMultiple(ratios);
                disonance += Math.log(lcm)/ Math.log(2);
            }
        }
        if(nbrOfNotes == 0)
            return 0;

        double averageDis = disonance / nbrOfNotes;
        if (averageDis > 1.0){
            return 1 / averageDis;
        } else {
            return 1 - averageDis;
        }
    }

    /**
     * Gets GCD of a list of integer values, uses gcd method on the result ofeach pairs wiuth the next
     * integer in the array
     * 
     * @param values
     * @return
     */
    private Integer gcdMultiple(List<Integer> values){
        if(values.size() > 0){
            int gcd = values.get(0);
            for(int i = 1; i < values.size(); i++){
                gcd = gcd(gcd, values.get(i));
            }
            return gcd;
        }
        return 0;
        
    }
    
    /**
     * GCD
     * 
     * @param first
     * @param second
     * @return
     */
    private Integer gcd(Integer first, Integer second){
        int temp;  
        while (first!=second){
            if(first < second){ 
              temp = first;
              first = second;
              second = temp;
            }
            first -= second;
        }
        return first;
    }
    

    /**
     * Similar to to gcdMultiple
     * 
     * @param values
     * @return
     */
    private Integer lcmMultiple(List<Integer> values){
        if(values.size() > 0){
            int lcm = values.get(0);
            for(int i = 1; i < values.size(); i++){
                lcm = lcm(lcm, values.get(i));
            }
            return lcm;
        }
        return 0;
    }
        
    /**
     * LCM
     * 
     * @param first
     * @param second
     * @return
     */
    private Integer lcm(Integer first, Integer second){ 
        return(first * second / (gcd(first, second)));
    }
}
