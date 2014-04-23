package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import org.junit.BeforeClass;
import org.junit.Test;
import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.model.geneticAlgorithm.rating.ZipfsLawRater;

public class ZipfsLawRaterTest {
    private static Song goodSong, badSong, randomSong;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new ZipfsLawRater(1.0);
        Phrase phrase1 = new Phrase(1.0);
        Phrase phrase2 = new Phrase(1.0);
        Phrase phrase3 = new Phrase(1.0);
        
        for(int i = 0; i < 2000; i++){
            if(i < 1000)
                phrase1.addNote(1, 1.0);
            else if(i < 1500)
                phrase1.addNote(2, 1.0);
            else if(i < 1830)
                phrase2.addNote(3, 1.0);
            else
                phrase2.addNote(4, 1.0);
            
            if(i < 1000)
                phrase2.addNote(1, 1.0);
            else
                phrase2.addNote(i, 1.0);
            phrase3.addNote(Math.round((float)Math.random() * 100), 1.0);
        }
        
        goodSong = new Song(new Score(new Part(phrase1)));
        badSong = new Song(new Score(new Part(phrase2)));
        randomSong = new Song(new Score(new Part(phrase3)));
    }
    
    @Test
    public void shouldRateSame(){
        double rating1 = rater.rate(randomSong);
        double rating2 = rater.rate(randomSong);
        assertTrue("Should rate same", rating1 == rating2);
    }
    
    @Test
    public void shouldRateBetter(){
        double ratingGood = rater.rate(goodSong);
        double ratingBad  = rater.rate(badSong);
        assertTrue("Song following zipfs law should get better rating", ratingGood > ratingBad);
    }
    
    @Test
    public void shouldRatePerfect(){
        double rating = rater.rate(goodSong);
        assertTrue("should get rating 1.0", rating == 1.0);
    }
    

}
