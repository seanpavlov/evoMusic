package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.CrazyNoteOctaveRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class CrazyNoteOctaveRaterTest {
    private static Song testSong, perfectSong, worstSong;
    
    private static SubRater rater;
    
    @BeforeClass
    public static void setUpSongs(){
        rater = new CrazyNoteOctaveRater(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        Phrase phrase3 = new Phrase(0.0);
        
        for(int i = 0; i < 20; i++){
            int j = i;
            if(i % 19 == 0)
                j = i + ((int)(Math.random() * 104) + 24 + i);
            
            if(i % 2 == 0)
                phrase3.addNote(0, 1.0);
            else
                phrase3.addNote(127, 1.0);
            phrase.addNote(j, 1.0); 
            phrase2.addNote(i, 1.0);
        }
        testSong  = new Song(new Score(new Part(phrase))); 
        testSong.addTagToTrack(0, TrackTag.MELODY);
        perfectSong = new Song(new Score(new Part(phrase2)));
        perfectSong.addTagToTrack(0, TrackTag.MELODY);
        worstSong = new Song(new Score(new Part(phrase3)));
        worstSong.addTagToTrack(0, TrackTag.MELODY);
    }
    
    @Test
    public void shouldRateSame(){
        double rating1 = rater.rate(testSong);
        double rating2 = rater.rate(testSong);
        assertTrue("same song should get same rating", rating1 == rating2);
    }
    
    @Test
    public void shouldNotGetPerfectRating(){
        double rating = rater.rate(testSong);
        assertFalse("Should not ger perfect rating", rating == 1.0);
    }
    
    @Test
    public void shouldGetWorstRating(){
        double rating = rater.rate(worstSong);
        assertTrue("Should get worst rating", rating == 0.0);
    }
    
    @Test
    public void shouldRatePerfect(){
        double rating = rater.rate(perfectSong);
        assertTrue("Should get perfect rating", rating == 1.0);
    }
}
