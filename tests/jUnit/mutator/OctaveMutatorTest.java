package jUnit.mutator;

import static org.junit.Assert.*;
import jm.music.data.Note;
import jm.music.data.Part;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.mutation.OctaveMutator;
import com.evoMusic.util.TrackTag;

public class OctaveMutatorTest {

    Song testSong;

    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     */
    @Before
    public void setUpSong() {
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_bros_theme.mid");
        for (Part part : testSong.getScore().getPartArray()) {
            testSong.addTagToTrack(part, TrackTag.MELODY);
        }
    }
    
    @Test
    public void testRangeWithinRange(){
        int testRange = 2;
        Note[] notes = testSong.getScore().getPart(0).getPhrase(0).getNoteArray();
        boolean breakedRule = false;
        int thisStep = 0;
        repeatedTest: for(int j = 0; j < 1000; j++){
            int nbrOfNotes = notes.length;
            OctaveMutator om = new OctaveMutator(1, testRange);
            findCandidate: for(int i = 0; i < nbrOfNotes; i++){
                if(notes[i].getPitch() >= 0){
                    om.mutate(testSong, i);
                    break findCandidate;
                }
            }
            thisStep = om.getNbrOfSteps();
            if(om.getNbrOfSteps() > testRange || om.getNbrOfSteps() <= 0){
                breakedRule = true;
                break repeatedTest;
            }
        }
        assertTrue("Given range: " + testRange + "\nGenerated Range: " + thisStep, !breakedRule);
    }
    
    @Test
    public void testOctave(){
        Note[] notes = testSong.getScore().getPart(0).getPhrase(0).getNoteArray();
        int nbrOfNotes = notes.length;
        OctaveMutator om = new OctaveMutator(1, 2);
        int oldPitch = 0;
        int newPitch = 0;
        findCandidate: for(int i = 0; i < nbrOfNotes; i++){
            if(notes[i].getPitch() >= 0){
                oldPitch = notes[i].getPitch();
                om.mutate(testSong, i);
                newPitch = testSong.getScore().getPart(0).getPhrase(0).getNoteArray()[i].getPitch();
                break findCandidate;
            }
        }
        assertTrue("Old Pitch:\t" + oldPitch + "\nOld Pitch(mod12):\t" + oldPitch%12 +
                "\nNew Pitch:\t" + newPitch + "\nNew Pitch(mod12):\t" + newPitch%12, oldPitch%12 == newPitch%12);
    }

}
