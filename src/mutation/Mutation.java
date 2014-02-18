package mutation;

import java.util.Vector;

import jm.music.data.Note;
import jm.music.data.Phrase;
import model.Song;

public class Mutation {
    private double mutationProbability;

    public Mutation(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public Song mutate(Song individual) {
        Phrase currentTrack = individual.getScore().getPart(0).getPhrase(0);
        int nbrOfNotes = currentTrack.getNoteArray().length;
        for (int i = 0; i < nbrOfNotes; i++) {
            if(Math.random() < mutationProbability){
                int newPitch = (int)((Math.random()*50)+50);
                Note newNote = new Note(newPitch, currentTrack.getNoteArray()[i].getRhythmValue());
                currentTrack.setNote(newNote, i);
            }
        }
        Vector newPhraseList = new Vector();
        newPhraseList.add(currentTrack);
        individual.getScore().getPart(0).setPhraseList(newPhraseList);
        return individual;
    }

}
