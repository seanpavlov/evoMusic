package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyDirectionRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class MelodyDirectionRaterTest {
    private static Song testSong, testSongWithChanges;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new MelodyDirectionRater(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        
        for(int i = 2; i < 22; i++){
            phrase.addNote(i, 1.0);
            if(i % 2 == 0)
                phrase2.addNote(i, 1.0);
            else
                phrase2.addNote(i-2, 1.0);
        }
        Part part = new Part(phrase);
        testSong  = new Song(new Score(part));
        testSong.addTagToTrack(0, TrackTag.MELODY);
        
        Part part2 = new Part(phrase2);
        testSongWithChanges = new Song(new Score(part2));
        testSongWithChanges.addTagToTrack(0, TrackTag.MELODY);
    }
    
    @Test
    public void shouldGetWorstRating(){
        double rating = rater.rate(testSong);
        assertTrue("Should get worst rating", rating == 0.0);
    }
    
    @Test
    public void shouldNotGetWorstRating(){
        double rating = rater.rate(testSongWithChanges);
        assertTrue("Should get perfect rating", rating > 0.0);
    }
}
