package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;

import jm.music.data.Note;

import com.evoMusic.model.Song;
import com.evoMusic.util.Sort;

public class NoOnlyRestRater extends SubRater{

    public NoOnlyRestRater(double weight){
        this.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        double silenceLength = 0;
        double songDuration = song.getScore().getEndTime();

        for (List<Note> simNotes : Sort.getSortedNoteList(song)){
            boolean isAllRest = true;
            double currentSilence = Integer.MAX_VALUE;
            
            for (Note n : simNotes){
                if (!n.isRest()){
                    isAllRest = false;
                    break;
                } else {
                    if (n.getDuration() < currentSilence){
                        currentSilence = n.getDuration();
                    }
                }
            }
            
            if (isAllRest){
                silenceLength += currentSilence;
                System.out.println(silenceLength);
            }
        }
        
        double rating = 1 - silenceLength / songDuration;
        return rating;
    }
}
