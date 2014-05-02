package com.evoMusic.model.geneticAlgorithm;

import jUnit.SongTest;

import java.util.Random;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import jm.music.data.Score;

public class RandomInitiator {
    private Song song;
    double maxLength;
    Random random = new Random();
    
    public RandomInitiator(double maxLength){
        this.song = new Song(new Score());
        song.getScore().setTempo(120);
        this.maxLength = maxLength;
    }
    
    /**
     * Generates a MelodyTrack and returns the song in which 
     * this track is added to.
     * 
     * @return
     */
    public Song generateMelody(){
        Part melodyPart = new Part();
        Track melodyTrack = new Track(melodyPart);
        int nrOfPhrases = (int) Math.random() * 6 + 1; 
        
        //randomize number of phrases
        for (int i = 0; i < nrOfPhrases; i++){
            Phrase phrase = new Phrase();
            phrase.setStartTime(0);
            
            //add notes while length is less than maxlength
            do {
                Note n = new Note();
                double noteLength = (random.nextInt(4) + 1) / 2;
                n.setDuration(noteLength);
                n.setRhythmValue(noteLength);

                if (random.nextInt(10) + 1 % 10 == 0){
                    n.setPitch(Note.REST);
                } else {
                    n.setPitch(random.nextInt(80) + 22);
                }
                n.setDynamic(64);
                phrase.add(n);
            } while (phrase.getEndTime() < maxLength);
            
            melodyPart.add(phrase);
        }

        melodyTrack.setTag(TrackTag.MELODY);
        song.addTrack(melodyTrack);
        return song;
    }
}
