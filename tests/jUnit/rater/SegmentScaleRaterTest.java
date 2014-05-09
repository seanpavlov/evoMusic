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

    @Test
    public void testRate() {
        Song s = Helpers.createSongWithMelody(new int[]{1,3,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1});
        s.addTagToTrack(0, TrackTag.MELODY);
        SubRater r = new SegmentScaleRater(1);
        SubRater r2 = new ScaleWhizz(1);

        assertTrue("should rate higher than scalewhizz", r.rate(s) > r2.rate(s));
        System.out.println(r.rate(s));
        s = Helpers.createSongWithMelody(new int[]{2,3,1,1,1,1,1,1,2,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
                1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1});
        s.addTagToTrack(0, TrackTag.MELODY);
        System.out.println(r.rate(s));
        assertTrue("should rate lower than scalewhizz", r.rate(s) < r2.rate(s));

    }

}
