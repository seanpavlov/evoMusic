package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.NoSilenceRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class NoSilenceRaterTest {
 private Song badPhraseSong, goodPhraseSong, badRestSong, goodRestSong;
    
    private SubRater rater;
    
    /**
     * Create songs, scores, part and phrases also initiate the 
     * phrases so that they fit the testing 
     */
    @Before
    public void setUpSong(){
       // create phrase songs
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
       
       badPhraseSong = new Song(badScore);
       goodPhraseSong = new Song(goodScore);
       
       //create rest songs
       Phrase good1 = new Phrase();
       good1.setStartTime(0);
       Phrase good2 = new Phrase();
       good2.setStartTime(0);
       Phrase bad1 = new Phrase();
       bad1.setStartTime(0);
       Phrase bad2 = new Phrase();
       bad2.setStartTime(0);
       for (int i = 0; i < 100; i++){
           if (i <= 60 && i >= 40){
               Note badN = new Note(Note.REST, 1);
               bad1.addNote(badN);
               bad2.addNote(badN);
            
               Note goodN = new Note(64, 1);
               good1.addNote(goodN);
               good2.addNote(goodN);
           
           } else {
               Note n = new Note(64, 1);
               bad1.addNote(n);
               bad2.addNote(n);
               good1.addNote(n);
               good2.addNote(n);
            }
       }
       
       Part goodP = new Part();
       goodP.add(good1);
       goodP.add(good2);
       
       Score goodS = new Score(goodP);
       goodRestSong = new Song(goodS);
       goodRestSong.addTagToTrack(0, TrackTag.MELODY);

       Part badP = new Part();
       badP.add(bad1);
       badP.add(bad2);
       Score badS = new Score(badP);
       badRestSong = new Song(badS);
       badRestSong.addTagToTrack(0, TrackTag.MELODY);       
    }    
    
    /**
     * Test that the same song always gets the same rating
     * */
    @Test
    public void testSameRating(){
        double rating1 = rater.rate(goodPhraseSong);
        double rating2 = rater.rate(goodPhraseSong);
        assertTrue("Rating value should be same for same song twice", rating1 == rating2);
    
        double rating3 = rater.rate(goodRestSong);
        double rating4 = rater.rate(goodRestSong);
        assertTrue("Rating value should be same for same song twice", rating3 == rating4);
    }
    
    /**
     * Test that good song is better than bad song
     * */
    @Test
    public void testBetterRating(){
        double rating1 = rater.rate(goodPhraseSong);
        double rating2 = rater.rate(badPhraseSong);
        System.out.println("good: " + rating1);
        System.out.println("bad: " + rating2);
        assertTrue("Rating value should be same for same song twice", rating1 > rating2);

        double rating3 = rater.rate(goodRestSong);
        double rating4 = rater.rate(badRestSong);
        System.out.println("good: " + rating3);
        System.out.println("bad: " + rating4);
        assertTrue("Rating value should be same for same song twice", rating3 > rating4);
    }
}
