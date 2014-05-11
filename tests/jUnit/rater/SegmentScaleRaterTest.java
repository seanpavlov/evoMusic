package jUnit.rater;

import static org.junit.Assert.*;
import jUnit.Helpers;

import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.ScaleWhizz;
import com.evoMusic.model.geneticAlgorithm.rating.SegmentScaleRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class SegmentScaleRaterTest {
    private SubRater r = new SegmentScaleRater(1);
    private SubRater r2 = new ScaleWhizz(1);
    
    private Song s;
    
    @Test
    public void testRate1() {
        SubRater r = new SegmentScaleRater(1);
        
        Song s = Helpers.createSongWithMelody(new int[]{1,1,1,1,1,1,1,1,1,1});
        s.addTagToTrack(0, TrackTag.MELODY);
        assertEquals("should rate 1", 1, r.rate(s), 1E-10);
        
    }
    
    @Test
    public void testRate2() {
        s = Helpers.createSongWithMelody(new int[]{1,3,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1});
        s.addTagToTrack(0, TrackTag.MELODY);
        assertTrue("should rate higher than scalewhizz", r.rate(s) > r2.rate(s));
        
    }
    
}
