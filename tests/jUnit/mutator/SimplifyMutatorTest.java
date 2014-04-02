package jUnit.mutator;

import static org.junit.Assert.assertTrue;
import jUnit.Helpers;
import jm.music.data.Note;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.SimplifyMutator;
import com.evoMusic.util.MidiUtil;

public class SimplifyMutatorTest {

    Song testSong;

    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     */
    @Before
    public void setUpSong() {
        testSong = Helpers.createTestSong();
    }
    
    @Test
    public void testSimplify(){
        MidiUtil mu = new MidiUtil();
        int testRange = 2;
        int nbrOfTestings = 1000;
        Note[] notes = testSong.getTrack(0).getPart().getPhrase(0).getNoteArray();
        int nbrOfNotes = notes.length;
        int nbrOfNeighbours = 0;
        int candidateIndex = 0;
        int breakedIndex = 0;
        boolean breakedRule = false;
        boolean reachedEnd = false;
        for(int i = 0; i < nbrOfTestings; i++){
            breakedRule = false;
            reachedEnd = false;
            nbrOfNeighbours = (int)(Math.random()*4)+1;
            SimplifyMutator sm = new SimplifyMutator(1, nbrOfNeighbours, 1);
            findCandidate: for(int j = 0; j < nbrOfNotes; j++){
                if(notes[j].getPitch() >= 0){
                    candidateIndex = j;
                    sm.mutate(testSong, j);
                    break findCandidate;
                }
            }
            int nbrOfPassed = 0;
            untilBreak: for(int k = candidateIndex; k >= 0; k--){
                if(notes[k].getPitch() == notes[candidateIndex].getPitch()){
                    nbrOfPassed++;
                }else if(!mu.isBlank(notes[k].getPitch())){
                    breakedRule = true;
                    breakedIndex = k;
                    break untilBreak;
                }
                if(k == 0){
                    reachedEnd = true;
                }
            }
            if(!reachedEnd){
                if(nbrOfPassed == nbrOfNeighbours){
                    breakedRule = true;
                }
            }
        }
        assertTrue("Note to be copied: " + notes[candidateIndex] + "\tPitch: " + notes[candidateIndex].getPitch() + "\nNote that differed: " + notes[breakedIndex] + "\tPitch: " + notes[breakedIndex].getPitch(), !breakedRule);
    }
}
