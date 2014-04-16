package jUnit.mutator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.mutation.OctaveMutator;
import com.evoMusic.util.MidiUtil;



public class OctaveMutatorTest {

    Song originSong;
    Song mutatedSong;
    MidiUtil mu;

    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     */
    @Before
    public void setUpSong() {
        originSong = Translator.INSTANCE.loadMidiToSong("midifiles/test_piano.mid");
        mutatedSong = Translator.INSTANCE.loadMidiToSong("midifiles/test_piano.mid");
    }
    
    @Before
    public void setUpUtil(){
        mu = new MidiUtil();
    }
    
    @Test
    public void mutationsWithinRange(){
        boolean testIsOkay = true;
        int stepRange = 2;
        OctaveMutator om = new OctaveMutator(1, stepRange);
        om.mutate(mutatedSong);
        int nbrOfTracks = originSong.getScore().getSize();
        int originPitch = 0;
        int mutatedPitch = 0;
        for(int track = 0; track < nbrOfTracks; track++){
            int nbrOfPhrases = originSong.getScore().getPart(track).getSize();
            for(int phrase = 0; phrase < nbrOfPhrases; phrase++){
                int nbrOfNotes = originSong.getScore().getPart(track).getPhrase(phrase).getSize();
                for(int note = 0; note < nbrOfNotes; note++){
                    originPitch = originSong.getScore().getPart(track).getPhrase(phrase).getNote(note).getPitch();
                    mutatedPitch = mutatedSong.getScore().getPart(track).getPhrase(phrase).getNote(note).getPitch();
                    if(originPitch%12 == mutatedPitch%12){
                        if(mu.getNotePitch(mutatedPitch) <= mu.getNotePitch(originPitch)+stepRange && mu.getNotePitch(mutatedPitch) >= mu.getNotePitch(originPitch)-stepRange){
                            
                        }else{
                            testIsOkay = false;
                        }
                    }else{
                        testIsOkay = false;
                    }
                }
            }
        }
        assertTrue("Original pitch:\t" + originPitch + "\nMutated Pitch:\t" + mutatedPitch, testIsOkay);
    }
}
