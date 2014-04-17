package jUnit.crossover;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.DrCross;
import com.evoMusic.util.TrackTag;
import com.google.common.collect.Lists;

public class DrCrossTest {

    @Test
    public void testLengthsAndSizes() {
        
        Song p1 = Translator.INSTANCE.loadMidiToSong("midifiles/Sweden.mid");
        p1.getTrack(0).setTag(TrackTag.MELODY);

        Song p2 = Translator.INSTANCE
                .loadMidiToSong("midifiles/johannesTestLOL.mid");
        p2.getTrack(0).setTag(TrackTag.MELODY);

        Song p3 = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        p3.getTrack(0).setTag(TrackTag.MELODY);

        List<Song> parents = Lists.<Song> newArrayList(p1, p2, p3);
        DrCross doctor = new DrCross(2, parents);

        List<Song> children = doctor.crossIndividuals();
        
        assertEquals("Children should be be same as the number of parents",
                children.size(), parents.size());
        
        double shortestParent = Double.MAX_VALUE;
        for (Song parent : parents) {
            if (parent.getScore().getEndTime() < shortestParent) {
                shortestParent = parent.getScore().getEndTime();
            }
        }
        
        for (int i = 0; i < children.size() - 1; i++) {
            assertEquals("All children should be the same length", 
                    children.get(i).getScore().getEndTime(), 
                    children.get(i+1).getScore().getEndTime(), 0.1);
            assertEquals("All children should have the same number of tracks",
                    children.get(i).getNbrOfTracks(), 
                    children.get(i+1).getNbrOfTracks());
        }
        
        assertEquals("Children should be the same length as the "
                + "shortest parent", 
                children.get(0).getScore().getEndTime(), shortestParent, 0.1);
        
    }
    
    @Test
    public void testCross() {

        Song p1 = Translator.INSTANCE.loadMidiToSong("midifiles/Sweden.mid");
        p1.getTrack(0).setTag(TrackTag.MELODY);

        Song p2 = Translator.INSTANCE
                .loadMidiToSong("midifiles/johannesTestLOL.mid");
        p2.getTrack(0).setTag(TrackTag.MELODY);

        Song p3 = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        p3.getTrack(0).setTag(TrackTag.BEAT);

        List<Song> parents = Lists.<Song> newArrayList(p1, p3, p2);
        DrCross doctor = new DrCross(4, parents);
        List<Song> children = doctor.crossIndividuals();
        
        assertEquals("Should not have any track tag BEAT", 
                0, children.get(0).getTaggedTracks(TrackTag.BEAT).size());

        assertEquals("Should not have any track tag MELODY", 
                0, children.get(0).getTaggedTracks(TrackTag.MELODY).size());

        
        p3.getTrack(0).setTag(TrackTag.MELODY);
        doctor.setParents(parents);
        children = doctor.crossIndividuals();
        Track segment = p1.getTrack(0).getSegment(8, 4);
        int occurences = 0;
        for (Song child : children) {
            if (child.getTaggedTracks(TrackTag.MELODY).size() != 0){
                if (child.getTaggedTracks(TrackTag.MELODY).get(0).getSegment(8, 4)
                        .equals(segment)) {
                    occurences++;
                }
            }
        }
        
        assertEquals("Should have exactly one occurence of the given segment",
                1, occurences);
    }
}
