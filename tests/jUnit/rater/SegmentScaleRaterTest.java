package jUnit.rater;

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
        Song s = Helpers.createSongWithMelody(new int[] {
                0, 2, 4, 5, 7, 5, 4, 2, 1, 3, 5, 6, 8, 6, 5, 3, 1});
        s.addTagToTrack(0, TrackTag.MELODY);
        SubRater r = new SegmentScaleRater();
        SubRater r2 = new ScaleWhizz(1);

        System.out.println("segScalew" + r.rate(s));
        System.out.println("Classic scalewhizz" + r2.rate(s));
        
    }

}
