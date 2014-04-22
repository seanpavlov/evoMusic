package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import jm.music.data.Note;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.util.MidiUtil;
import com.evoMusic.util.Sort;

public class RhythmValueMutator extends ISubMutator {
    double movingRange;
    
    public RhythmValueMutator(double mutationProbability, double movingRange) {
        super(mutationProbability);
        this.movingRange = movingRange;
    }

    @Override
    public void mutate(Song individual) {
        Random ra = new Random();
        int nbrOfTracks = individual.getScore().getPartArray().length;
        double[][] currentNoteTime = new double[nbrOfTracks][100];
        // Tracks
        for (int track = 0; track < nbrOfTracks; track++) {
            List<List<Note>> noteList = Sort.getSortedNoteList(individual
                    .getScore().getPart(track));
            // For all elements in the list
            int nbrOfElements = noteList.size();
            for (int elem = 0; elem < nbrOfElements; elem++) {
                List<Note> parallelNoteList = noteList.get(elem);
                int nbrOfParallel = parallelNoteList.size();
                if (Math.random() < getProbability()) {
                    double movingLength = 0.5*(ra.nextInt(((int)(movingRange/0.5)))+1);
                    if(Math.random() < 0.5){
                        movingLength = -movingLength;
                    }
                    // For all parallel notes
                    for (int note = 0; note < nbrOfParallel; note++) {
                        double newStartTime = currentNoteTime[track][individual.getScore().getPart(track).getPhraseList().indexOf(parallelNoteList.get(note).getMyPhrase())] + movingLength;
                        if(!parallelNoteList.get(note).isRest()){
                            if(newStartTime >= 0 && newStartTime+parallelNoteList.get(note).getDuration() <= individual.getScore().getEndTime()){
                                Phrase newPhrase = new Phrase(newStartTime);
                                newPhrase.addNote(parallelNoteList.get(note).copy());
                                individual.getScore().getPart(track).addPhrase(newPhrase);
                                parallelNoteList.get(note).setPitch(Integer.MIN_VALUE);
                            }
                        }
                    }
                }
                //Add rhythm value to the array for each note that play
                for (int note = 0; note < nbrOfParallel; note++) {
                    currentNoteTime[track][individual.getScore().getPart(track).getPhraseList().indexOf(parallelNoteList.get(note).getMyPhrase())] += parallelNoteList.get(note).getRhythmValue();
                }
            }
        }
    }

}
