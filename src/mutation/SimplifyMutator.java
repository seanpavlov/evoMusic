package mutation;

import jm.music.data.Note;
import model.Song;

public class SimplifyMutator extends ISubMutator {

    private int nbrOfPastNeighbours;
    private double neighbourProbability;

    /**
     * Mutate so that past neighboring notes will have the same pitch as the
     * current node.
     * 
     * @param mutationProbability
     *            is the probability of this mutator to run.
     * @param nbrOfPastNeighbours
     *            is the number of past neighbors who can be mutated.
     * @param neighbourProbability
     *            is the probability of the specific neighbor to the mutated.
     */
    public SimplifyMutator(double mutationProbability, int nbrOfPastNeighbours,
            double neighbourProbability) {
        super(mutationProbability);
        this.nbrOfPastNeighbours = nbrOfPastNeighbours;
        this.neighbourProbability = neighbourProbability;
    }

    /**
     * Mutate the note with noteIndex of song.
     */
    @Override
    public void mutate(Song song, int noteIndex) {
        Note note = song.getScore().getPart(0).getPhrase(0).getNote(noteIndex);
        if (Math.random() < this.getProbability()) {
            MidiUtil mu = new MidiUtil();
            int currentNoteIndex = noteIndex - 1;
            if(currentNoteIndex >= 0){
                noteIteration: for (int i = 0; i < nbrOfPastNeighbours; i++) {
                    System.out.println("Note: " + noteIndex + "\t Current: " + currentNoteIndex + "\t Pitch: " + note.getPitch());
                    while (mu.isBlank(song.getScore().getPart(0).getPhrase(0).getNote(currentNoteIndex).getPitch()) && currentNoteIndex >= 0) {
                        currentNoteIndex--;
                    }
                    if (currentNoteIndex < 0) {
                        break noteIteration;
                    } else {
                        if (Math.random() < neighbourProbability) {
                            song.getScore().getPart(0).getPhrase(0)
                                    .getNote(currentNoteIndex)
                                    .setPitch(note.getPitch());
                        }
                    }
                }
            }
        }
    }

}
