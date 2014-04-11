package jUnit;

import jm.music.data.Part;

import org.junit.Test;

import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;

public class TrackTest {

    @Test
    public void test() {
        Track trackOriginal = Helpers.createSongWithMelody(
                new int[] { 8 + 12 * 4, 8 + 12 * 4, 8 + 12 * 4, 8 + 12 * 4 })
                .getTrack(0);
        Track trackInsert = Helpers.createSongWithMelody(
                new int[] { 0 + 12 * 4, 1 + 12 * 4, 2 + 12 * 4, 3 + 12 * 4 })
                .getTrack(0);

        Track newTrack = new Track(new Part());
        
        newTrack.merge(trackOriginal, 4);
        newTrack.merge(trackInsert, 0);
        newTrack.printRoll();
    }

}
