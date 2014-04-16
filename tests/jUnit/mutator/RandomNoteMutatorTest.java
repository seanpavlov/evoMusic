package jUnit.mutator;

import org.junit.Test;



public class RandomNoteMutatorTest {

    //Song testSong;

    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     */
    /*@Before
    public void setUpSong() {
        testSong = Helpers.createTestSong();
    }

    @Test
    public void testRandomNote() {
        int testRange = 2;
        Note[] notes = testSong.getTrack(0).getPart().getPhrase(0)
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
        int newPitch = testSong.getTrack(0).getPart().getPhrase(0)
                .getNote(candidateIndex).getPitch();
        assertTrue("Old Pitch: " + oldPitch + "\nNewPitch: " + newPitch,
                newPitch <= oldPitch + testRange
                        && newPitch >= oldPitch - testRange);
    }*/

    @Test
    public void testDecreasingProbability() {

    }
}
