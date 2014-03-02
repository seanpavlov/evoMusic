package mutation;

import jm.music.data.Note;
import model.Song;

public class ScaleOfFifthMutator extends ISubMutator {
    private int stepRange;

    public ScaleOfFifthMutator(double mutationProbability, int stepRange) {
        super(mutationProbability);
        this.stepRange = stepRange;
    }

    /**
     * Mutate the note in the scale of fifth, which is five half steps in either
     * direction.
     */
    @Override
    public void mutate(Song song, int noteIndex) {
        if (Math.random() < this.getProbability()) {
            MidiUtil mu = new MidiUtil();
            Note note = song.getScore().getPart(0).getPhrase(0).getNote(noteIndex);
            int nbrOfSteps = (int) ((Math.random() * stepRange) + 1);
            int pitchNbr = note.getPitch();
            System.out.print("Old pitch: " + note.getPitch());
            if (!mu.isBlank(pitchNbr)) {
                if (mu.canRaiseNote(pitchNbr, nbrOfSteps)) {
                    if (mu.canLowerNote(pitchNbr, nbrOfSteps)) {
                        if (Math.random() < 0.5) {
                            note.setPitch(pitchNbr - (5 * nbrOfSteps));
                        } else {
                            note.setPitch(pitchNbr + (5 * nbrOfSteps));
                        }
                    } else {
                        note.setPitch(pitchNbr + (5 * nbrOfSteps));
                    }
                } else if (mu.canLowerNote(pitchNbr, nbrOfSteps)) {
                    note.setPitch(pitchNbr - (5 * nbrOfSteps));
                }
                System.out.println("New pitch: " + note.getPitch());
                song.getScore().getPart(0).getPhrase(0)
                        .setNote(note, noteIndex);
            }
        }
    }

}
