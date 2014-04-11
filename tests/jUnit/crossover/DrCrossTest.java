package jUnit.crossover;

import jUnit.Helpers;

import java.util.List;

import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.DrCross;
import com.evoMusic.util.TrackTag;
import com.google.common.collect.Lists;

public class DrCrossTest {

    @Test
    public void test() {
//        Song p1 = Helpers.createTestSong();
        Song p1 = Helpers.createSongWithMelody(new int[]{1+12*5, 1+12*5, 1+12*5, 1+12*5});
        p1.addTagToTrack(0, TrackTag.MELODY);
        Song p2 = Helpers.createSongWithMelody(new int[]{0+12*5, 0+12*5, 0+12*5, 0+12*5, 0+12*5, 0+12*5, 0+12*5, 0+12*5});
        p2.addTagToTrack(0, TrackTag.MELODY);
        DrCross doctor = new DrCross(2d, Lists.<Song>newArrayList(p1, p2));
        
        List<Song> children = doctor.crossIndividuals();
        children.get(0).getTrack(0).printRoll();
//        children.get(1).getTrack(0).printRoll();
        Translator.INSTANCE.playSong(children.get(0));
//        Translator.INSTANCE.playSong(children.get(1));
    }

}
