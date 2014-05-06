package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.Random;

import jm.music.data.Note;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

public class RhythmValueMutator extends ISubMutator {
    double movingRange;
    
    public RhythmValueMutator(double mutationProbability, double movingRange) {
        super(mutationProbability);
        this.movingRange = movingRange;
    }

    @Override
    public void mutate(Song individual) {
        System.out.println("MUTATION");
        Random ra = new Random();

        for (Track track : individual.getTracks()) {
            double[] currentPhraseTime = new double[track.getPart().getSize()];
            for(Phrase phrase : track.getPart().getPhraseArray()){
                for(Note note : phrase.getNoteArray()) {
                    if (Math.random() < getProbability()) {
                        double movingLength = 0.5 * (ra.nextInt(((int)(movingRange/0.5)))+1);
                        if(Math.random() < 0.5){
                            movingLength = -movingLength;
                        }
                        if(!note.isRest()){
                            double newStartTime = currentPhraseTime[track.getPart().getPhraseList().indexOf(phrase)] + movingLength;
                            if(newStartTime >= 0 && newStartTime + note.getDuration() <= individual.getScore().getEndTime()){
                                Phrase newPhrase = new Phrase(newStartTime);
                                newPhrase.addNote(note.copy());
                                track.getPart().addPhrase(newPhrase);
                                note.setPitch(Integer.MIN_VALUE);
                            }
                        }
                        
                    }
                    currentPhraseTime[track.getPart().getPhraseList().indexOf(phrase)] += note.getRhythmValue();
                }
            }
        }
        individual.flattern();
    }
}
