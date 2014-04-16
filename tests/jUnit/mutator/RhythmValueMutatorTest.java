package jUnit.mutator;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.mutation.RhythmValueMutator;
import com.evoMusic.util.MidiUtil;



public class RhythmValueMutatorTest {

    Song originSong;
    Song mutatedSong;
    MidiUtil mu;
    int nbrOfMutatedNotes = 0;

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
    public void rhythmValueMutatorTest(){
        boolean testIsOkay = true;
        RhythmValueMutator rvm = new RhythmValueMutator(1, 1);
        rvm.mutate(mutatedSong);
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
                    if(originPitch != mutatedPitch){
                       if(mu.isBlank(mutatedPitch)){
                           nbrOfMutatedNotes++;
                       }else{
                           testIsOkay = false;
                       }
                    }
                }
            }
        }
        assertTrue("Original pitch:\t" + originPitch + "\nMutated Pitch:\t" + mutatedPitch, testIsOkay);
        int nbrOfMutatedSongPhrases = 0;
        int nbrOfOriginSongPhrases = 0;
        for(int track = 0; track < nbrOfTracks; track++){
            System.out.println("Mu:\t" + track + "\t" + mutatedSong.getScore().getPart(track).getSize());
            System.out.println("Mu:\t" + track + "\t" + originSong.getScore().getPart(track).getSize());
            nbrOfMutatedSongPhrases += mutatedSong.getScore().getPart(track).getSize();
            nbrOfOriginSongPhrases += originSong.getScore().getPart(track).getSize();
        }
        assertTrue("Mutations:\t" + nbrOfMutatedNotes + "\nNew phrases:\t" + (nbrOfMutatedSongPhrases-nbrOfOriginSongPhrases), nbrOfMutatedNotes == (nbrOfMutatedSongPhrases-nbrOfOriginSongPhrases));
    }
}
