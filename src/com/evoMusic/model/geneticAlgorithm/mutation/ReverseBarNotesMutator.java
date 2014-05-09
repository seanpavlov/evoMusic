package com.evoMusic.model.geneticAlgorithm.mutation;



import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

public class ReverseBarNotesMutator extends ISubMutator {

    /**
     * Reverse a part of the track by selecting a number of notes and put them
     * in the song in reversed order again.
     * 
     * @param mutationProbability
     *            is the probability of the mutation.
     * @param nbrOfAdditionalReversing
     *            is the number of neighboring notes to current note that is
     *            going to be part of of the reversing.
     * @param nbrRange
     *            is the range of neighboring notes it can be. It will range
     *            between nbrOfAdditionalReversing and
     *            nbrOfAdditionalReversing+nbrRange.
     * @param withRhythmLength
     *            is if it should take the rhythm length into the reversing.
     *            True if it should and false otherwise.
     */
    public ReverseBarNotesMutator(double mutationProbability) {
        super(mutationProbability);
    }

    /**
     * Mutate the note with noteIndex of song.
     */
    @Override
    public void mutate(Song individual, double probabilityMultiplier) {
        int nbrOfTracks = individual.getScore().getSize();
        double localProbability = getProbability()*probabilityMultiplier;
        for (int track = 0; track < nbrOfTracks; track++) {
            double nbrOfBars = individual.getScore().getPart(track).getEndTime() / 4.0;
            Track currentTrack = new Track(individual.getScore().getPart(track));
            for (int bar = 0; bar < nbrOfBars; bar++) {
                if(Math.random() < localProbability){
                    Track currentBar = currentTrack.getSegment(bar*4, 4);
                    int nbrOfPhrases = currentBar.getPart().size();
                    for(int phrase = 0; phrase < nbrOfPhrases; phrase++){
                        int nbrOfNotes = currentBar.getPart().getPhrase(phrase).size();
                        for(int note = 0; note < nbrOfNotes/2; note++){
                            int tempPitch = currentBar.getPart().getPhrase(phrase).getNote(note).getPitch();
                            currentBar.getPart().getPhrase(phrase).getNote(note).setPitch(currentBar.getPart().getPhrase(phrase).getNote(nbrOfNotes-note-1).getPitch());
                            currentBar.getPart().getPhrase(phrase).getNote(nbrOfNotes-note-1).setPitch(tempPitch);
                        }
                    }
                }
            }
        }
    }
}
