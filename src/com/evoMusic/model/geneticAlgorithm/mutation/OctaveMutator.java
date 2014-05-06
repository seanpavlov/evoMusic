package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import jm.music.data.Note;

import com.evoMusic.model.Song;
import com.evoMusic.util.MidiUtil;
import com.evoMusic.util.Sort;

public class OctaveMutator extends ISubMutator {
    private int octaveRange;

    /**
     * Raises or lower the note pitch in steps of octaves.
     * 
     * @param mutationProbability
     *            is the probability of mutation.
     * @param octaveRange
     *            is the range that it can change in octave.
     */
    public OctaveMutator(double mutationProbability, int octaveRange) {
        super(mutationProbability);
        this.octaveRange = octaveRange;
    }

    /**
     * Mutate the note with noteIndex of song.
     */
    @Override
    public void mutate(Song individual, double probabilityMultiplier) {
        MidiUtil mu = new MidiUtil();
        Random ra = new Random();
        double localProbability = getProbability()*probabilityMultiplier;

        int nbrOfTracks = individual.getScore().getPartArray().length;
        for (int track = 0; track < nbrOfTracks; track++) {
            List<List<Note>> noteList = Sort.getSortedNoteList(individual
                    .getScore().getPart(track));
            int nbrOfNotes = noteList.size();
            for (int currentNoteId = 0; currentNoteId < nbrOfNotes; currentNoteId++) {
                if (Math.random() < localProbability) {
                    List<Note> paralellList = noteList.get(currentNoteId);
                    int nbrOfParalellNotes = paralellList.size();
                    int selectedPhrase = ra.nextInt(nbrOfParalellNotes);
                    Note currentNote = paralellList.get(selectedPhrase);
                    if (!mu.isBlank(currentNote.getPitch())) {
                        int nbrOfSteps = mu.NBR_OF_NOTES*((int)(Math.random()*octaveRange));
                        int pitchNbr = currentNote.getPitch();
                        if (mu.canRaiseNote(pitchNbr, nbrOfSteps)) {
                            if (mu.canLowerNote(pitchNbr, nbrOfSteps)) {
                                if (Math.random() < 0.5) {
                                    currentNote.setPitch(pitchNbr
                                            - nbrOfSteps);
                                } else {
                                    currentNote.setPitch(pitchNbr
                                            + nbrOfSteps);
                                }
                            } else {
                                currentNote.setPitch(pitchNbr + nbrOfSteps);
                            }
                        } else if (mu.canLowerNote(pitchNbr, nbrOfSteps)) {
                            currentNote.setPitch(pitchNbr - nbrOfSteps);
                        }
                    }
                }
            }
        }
    }
    
    public int getOctaveRange(){
        return octaveRange;
    }
    

}
