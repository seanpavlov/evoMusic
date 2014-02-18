package mutation;

import java.util.Vector;

import jm.music.data.Note;
import jm.music.data.Phrase;
import model.Song;

public class Mutation {
    private double mutationProbability;

    /**
     * Handling the mutation. Each instance will use their own parameters, then
     * it can be used for many songs if the user want to.
     * 
     * @param mutationProbability
     *            is the probability of the note to mutate.
     */
    public Mutation(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    /**
     * Mutates the song with the probability mutationProbability. The new note
     * is within the interval 50-100 right now.
     * 
     * @param individual
     *            is the song to be mutated.
     * @return the mutated song.
     */
    public Song mutate(Song individual) {
        Phrase currentTrack = individual.getScore().getPart(0).getPhrase(0);
        int nbrOfNotes = currentTrack.getNoteArray().length;
        for (int i = 0; i < nbrOfNotes; i++) {
            if (Math.random() < mutationProbability) {
                int newPitch = (int) ((Math.random() * 50) + 50);
                Note newNote = new Note(newPitch,
                        currentTrack.getNoteArray()[i].getRhythmValue());
                currentTrack.setNote(newNote, i);
            }
        }
        Vector newPhraseList = new Vector();
        newPhraseList.add(currentTrack);
        individual.getScore().getPart(0).setPhraseList(newPhraseList);
        return individual;
    }

}
