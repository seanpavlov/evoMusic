package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.NoSilenceRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;

public class NoSilenceRaterTest {
 private Song badSong, goodSong;
    
    private SubRater rater;
    
    /**
     * Create songs, scores, part and phrases also initiate the 
     * phrases so that they fit the testing 
     */
    @Before
    public void setUpSong(){
       rater = new NoSilenceRater(1);
       
       Score goodScore = new Score();
       Score badScore = new Score();
       
       
       Part badPart = new Part();
       Part goodPart = new Part();
       
       for (int i = 0 ; i < 10; i++){
           Phrase good = new Phrase();
           good.setStartTime(i);
           good.setDuration(1);
           
           Phrase bad = new Phrase();
           bad.setStartTime(i);
           bad.setDuration(i);
           if (i == 9){
               bad.setStartTime(i*10);
           }

           badPart.add(bad);
           goodPart.add(good);
       }
       
       goodScore.add(goodPart);
       badScore.add(badPart);
       
       badSong = new Song(badScore);
       goodSong = new Song(goodScore);
       
    }    
    
    /**
     * Test that the same song always gets the same rating
     * */
    @Test
    public void testSameRating(){
        double rating1 = rater.rate(goodSong);
        double rating2 = rater.rate(goodSong);
        assertTrue("Rating value should be same for same song twice", rating1 == rating2);
    }
    
    /**
     * Test that good song is better than bad song
     * */
    @Test
    public void testBetterRating(){
        double rating1 = rater.rate(goodSong);
        double rating2 = rater.rate(badSong);
        assertTrue("Rating value should be same for same song twice", rating1 > rating2);
    }
}
