package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class ChordRepetitionRaterTest {
    private Song testSongChordTags, testSongNoChordTags, testSong, perfectSong ;
    
    private SubRater rater;
    
    @Before
    public void setUpSong(){
       rater = new ChordRepetitionRater(1);
       
       Phrase[] phrases = {new Phrase(0.0),new Phrase(2.0),new Phrase(4.0),new Phrase(5.0)};
       
       for(int i = 0; i < phrases.length; i++){
           int[] notes = new int[4];
           for(int j = 0; j < notes.length; j++){
               notes[j] = j;
           }
           phrases[i].addChord(notes, 1.0);
       }
       
       
       
       
       Part part = new Part();
       part.addPhraseList(phrases);
       Score score = new Score(part);
       perfectSong = new Song(score);
       perfectSong.addTagToTrack(part, TrackTag.CHORDS);
       
       testSongNoChordTags = new Song(score);
       
    }
    
    @Test
    public void shouldGetRateZero(){
        double rating = rater.rate(testSongNoChordTags);
        assertTrue("should get 0.0 rating", rating == 0.0);
    }
    
    @Test
    public void shouldGetPerfectRating(){
        double rating = rater.rate(perfectSong);
        assertTrue("Should get rating 1.0", rating == 1.0);
    }
    
}
