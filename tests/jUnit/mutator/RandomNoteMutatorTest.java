package jUnit.mutator;

import static org.junit.Assert.assertTrue;
import jm.music.data.Note;
import jm.music.data.Part;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.mutation.RandomNoteMutator;
import com.evoMusic.util.MidiUtil;
import com.evoMusic.util.TrackTag;

public class RandomNoteMutatorTest {

    Song testSong;

    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     */
    @Before
    public void setUpSong() {
        testSong = Translator.INSTANCE
                .loadMidiToSong("midifiles/super_mario_bros_theme.mid");
        for (Part part : testSong.getScore().getPartArray()) {
            testSong.addTagToTrack(part, TrackTag.MELODY);
        }
    }

    @Test
    public void testRandomNote() {
        int testRange = 2;
        Note[] notes = testSong.getScore().getPart(0).getPhrase(0)
                .getNoteArray();
        int nbrOfNotes = notes.length;
        int candidateIndex = 0;
        int oldPitch = 0;
        MidiUtil mu = new MidiUtil();
        RandomNoteMutator rnm = new RandomNoteMutator(1, testRange);
        findCandidate: for (int j = 0; j < nbrOfNotes; j++) {
            if (!mu.isBlank(notes[j].getPitch())) {
                candidateIndex = j;
                oldPitch = notes[j].getPitch();
                rnm.mutate(testSong, j);
                break findCandidate;
            }
        }
        int newPitch = testSong.getScore().getPart(0).getPhrase(0)
                .getNote(candidateIndex).getPitch();
        assertTrue("Old Pitch: " + oldPitch + "\nNewPitch: " + newPitch,
                newPitch <= oldPitch + testRange
                        && newPitch >= oldPitch - testRange);
    }

    public void testDecreasingProbability() {

    }
}
