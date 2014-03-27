package jUnit.rater;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class BeatRaterTest {
    
    private Song testSongBeatTags, testSongNoBeatTags, testSong, perfectSong ;
    
    private SubRater rater;
    
    /**
     * Create songs instances with known rhythm values to be able to test rater
     */
    @Before
    public void setUpSong(){
       rater = new BeatRepetitionRater(1);
       
       Score rhythmScore = new Score("Rhythm");
       Part part = new Part("s", 0, 9);
       Phrase phr = new Phrase(0.0);
       
       Score rhythmScore2 = new Score("Rhythm2");
       Part part2 = new Part("s2", 0, 9);
       Phrase phr2 = new Phrase(0.0);
       
       Score rhythmScore3 = new Score("Rhythm3");
       Part part3 = new Part("s3", 0, 9);
       Phrase phr3 = new Phrase(0.0);
       
       List<double[]> patterns = new ArrayList<double[]>();
       /**Different patterns to use when creating test songs with known patterns in beat tracks*/
       double[] pattern0 = {1.0, 0.5, 0.5, 1.5, 0.5};
       double[] pattern1 = {0.5, 0.5, 1.5, 0.5, 1.0};
       double[] pattern2 = {2.0, 0.5, 0.5, 0.5, 0.5};
       double[] pattern3 = {1.5, 0.5, 1.0, 0.5, 0.5};
       double[] pattern;
       /**Values to use when creating test songs with random values in beat tracks
        * and for creating song with perfect patterns in beat track*/
       double[] patternValues = {0.0, 0.5, 1.0, 1.5, 2.0};
       patterns.add(pattern0);
       patterns.add(pattern1);
       patterns.add(pattern2);
       patterns.add(pattern3);
       
       for(int i = 0; i < 8; i++){
           int x = (int)(Math.random()*4);
           pattern = patterns.get(x);
           for(int j = 0; j<pattern.length; j++){
               /**Add next value in selected pattern*/
               Note note = new Note((int)(38), pattern[j]);
               phr.addNote(note);
               
               /**Add random rhythm value to note*/
               Note note2 = new Note((int)(38), patternValues[(int)(Math.random()*5)]);
               phr2.addNote(note2);
               
               /**Add next value from same pattern*/
               Note note3 = new Note((int)(38), patternValues[j]);
               phr3.add(note3);
           }
       }
       
       /**Set phrase to part and add to score and finally create new Song testSongBeatTags and tag part with BEAT*/
       part.addPhrase(phr);
       rhythmScore.addPart(part);
       testSongBeatTags = new Song(rhythmScore);
       testSongBeatTags.addTagToTrack(part, TrackTag.BEAT);
       
       /**Set phrase to part and add to score and finally create new Song testSongNoBeatTags*/
       part2.addPhrase(phr2);
       rhythmScore2.addPart(part2);
       testSongNoBeatTags = new Song(rhythmScore2);
       
       /**Set phrase to part and add to score and finally create new Song perfectSong and tag part with BEAT*/
       part3.add(phr3);
       rhythmScore3.add(part3);
       perfectSong = new Song(rhythmScore3);
       perfectSong.addTagToTrack(part3, TrackTag.BEAT);
       
       /**create new Song testSong and tag part with BEAT*/
       testSong = new Song(rhythmScore2);
       testSong.addTagToTrack(part2, TrackTag.BEAT);
    }
    
    /**
     * Test that the same song always gets the same rating
     * */
    @Test
    public void testSameRating(){
        double rating1 = rater.rate(testSongBeatTags);
        double rating2 = rater.rate(testSongBeatTags);
        assertTrue("Rating value should be same for same song twice", rating1 == rating2);
    }
    
    /**
     * Test that a song without a track tagged with Beat or Rhythm tag gets rating 0
     * */
    @Test
    public void testShouldRateZero(){
        double rating = rater.rate(testSongNoBeatTags);
        assertTrue("Song with no Rythm or Beat tag should rate 0", rating == 0.0);
    }
    
    /**
     * Test that song containing track, tagged with Beat tag, which contains patterns, gets rating higher than 0
     * */
    @Test 
    public void testShouldNotRateZero(){      
        double rating = rater.rate(testSongBeatTags);
        assertTrue("Song with beat tags at known beat parts should get higher rating than 0", rating > 0);
    }
    
    
    /**
     * Test that random valued beat track should rate less that song with known patterns in its beat track
     * */
    @Test
    public void testShouldRateLess(){
        double rating1 = rater.rate(testSong);
        double rating2 = rater.rate(testSongBeatTags);
        assertTrue("Random tagged beat parts should rate less than song with known beat parts", rating1 < rating2);
    }
    
    /**
     * Test that song with perfect pattern in its beat track should get perfect rating (1.0)
     * */
    @Test
    public void testShouldGetPerfectRating(){
        double rating = rater.rate(perfectSong);
        assertTrue("Song should get perfect rating", rating == 1.0);
    }
}