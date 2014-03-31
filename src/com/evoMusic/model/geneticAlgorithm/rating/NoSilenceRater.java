package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;
import jm.music.data.Phrase;
import com.evoMusic.model.Song;
import com.evoMusic.util.Sort;

public class NoSilenceRater extends SubRater {

    public NoSilenceRater(double weight){
        this.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        List<Phrase> allPhrases = Sort.INSTANCE.sortPhrasesOnStartTime(song);
        double songDuration = song.getScore().getEndTime();
        double longestSilence = 0.0001;
        
        for (int i = 0; i < allPhrases.size()-1; i++){
            Phrase thisPhrase = allPhrases.get(i);
            Phrase nextPhrase = allPhrases.get(i+1);
            double silenceInterval = nextPhrase.getStartTime() - thisPhrase.getEndTime();
            
            if (silenceInterval > longestSilence){
                longestSilence = silenceInterval;
            }
        }
        double rating = 1 - longestSilence / songDuration;
        return rating;
    }
}

        