package com.evoMusic.model.geneticAlgorithm.rating;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jm.music.data.Phrase;
import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

public class RhythmicVarietyRater extends SubRater{

    public RhythmicVarietyRater(double targetRating){
        this.setTargetRating(targetRating);
    }
    
    /**
     * This rater rates the frequencies of rhythm vales of the notes, the more unique rhythm values it finds
     * the better rating it provides.
     */
    @Override
    public double rate(Song song) {
        double rating;
        double numberOfNotes = 0;
        List<Track> tracks = song.getTracks();
        Map<Double,Integer> rhythmDistribution = new HashMap<Double, Integer>();
        for (Track track : tracks){
            for (Phrase phrase : track.getPart().getPhraseArray()){
                double[] rhythms =  phrase.getRhythmArray();
                numberOfNotes += rhythms.length;
                for (double rhythm : rhythms){
                    if (rhythmDistribution.containsKey(rhythm)){
                        int tempVal = rhythmDistribution.get(rhythm);
                        rhythmDistribution.put(rhythm, ++tempVal);
                    } else {
                        rhythmDistribution.put(rhythm, 1);
                    }
                }
            }
        }
        rating = (rhythmDistribution.size() / numberOfNotes);
        return rating;
    }
}
