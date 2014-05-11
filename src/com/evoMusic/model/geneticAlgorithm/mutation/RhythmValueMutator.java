package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jm.music.data.Note;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

public class RhythmValueMutator extends ISubMutator {
    private double movingRange;
    
    public RhythmValueMutator(double mutationProbability, double movingRange) {
        super(mutationProbability);
        this.movingRange = movingRange;
    }

    @Override
    public void mutate(Song individual, double probabilityMultiplier) {
        Random ra = new Random();
        double localProbability = getProbability()*probabilityMultiplier;
        List<Phrase> phrasesToBeAdded;
        double endTime = individual.getScore().getEndTime();
        Phrase newPhrase;
        
        double phraseTime;
        double newNoteTime;
        for (Track track : individual.getTracks()) {
            phrasesToBeAdded = new ArrayList<Phrase>();
            for (Phrase phrase : track.getPart().getPhraseArray()) {
                phraseTime = phrase.getStartTime();
                for (Note note : phrase.getNoteArray()) {
                    if (!note.isRest() && ra.nextDouble() < localProbability) {
                        double movingLength = 0.5 * (ra.nextInt(((int)(movingRange/0.5)))+1);
                        if(ra.nextDouble() < 0.5){
                            movingLength = -movingLength;
                        }
                        newNoteTime = phraseTime + movingLength;
                        if (newNoteTime >= 0 && newNoteTime + note.getRhythmValue() <= endTime) {
                            newPhrase = new Phrase(newNoteTime);
                            newPhrase.add(note.copy());
                            note.setPitch(Note.REST);
                            phrasesToBeAdded.add(newPhrase);
                        }
                    }
                    phraseTime += note.getRhythmValue();
                }
            }
            for (Phrase phrase : phrasesToBeAdded) {
                track.getPart().add(phrase);
            }
        }
        
        
        
        
//        for (Track track : individual.getTracks()) {
//            double[] currentPhraseTime = new double[track.getPart().getSize()];
//            for(Phrase phrase : track.getPart().getPhraseArray()){
//                for(Note note : phrase.getNoteArray()) {
//                    if (Math.random() < localProbability) {
//                        double movingLength = 0.5 * (ra.nextInt(((int)(movingRange/0.5)))+1);
//                        if(Math.random() < 0.5){
//                            movingLength = -movingLength;
//                        }
//                        if(!note.isRest()){
//                            double newStartTime = currentPhraseTime[track.getPart().getPhraseList().indexOf(phrase)] + movingLength;
//                            if(newStartTime >= 0 && newStartTime + note.getDuration() <= individual.getScore().getEndTime()){
//                                Phrase newPhrase = new Phrase(newStartTime);
//                                newPhrase.addNote(note.copy());
//                                track.getPart().addPhrase(newPhrase);
//                                note.setPitch(Note.REST);
//                            }
//                        }
//                        
//                    }
//                    currentPhraseTime[track.getPart().getPhraseList().indexOf(phrase)] += note.getRhythmValue();
//                }
//            }
//        }
    }
}
