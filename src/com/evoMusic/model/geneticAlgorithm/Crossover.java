package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.evoMusic.model.Song;
import com.evoMusic.model.enumerators.TrackTag;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

public class Crossover {

    private int numberOfIntersections;
    Random randomGen;

    /**
     * Creates a mutable Crossover object containing parents that are to be
     * crossed. The parents will not be modified by this class.
     * 
     * @param parents
     *            The Song objects that will be used during crossover.
     */
    public Crossover(int numberOfIntersections) {
        setNumberOfIntersections(numberOfIntersections);
        randomGen = new Random();
    }

    /**
     * Sets the number of intersections in which the crossover will swap
     * segments between parents. The default value is the number of parents
     * during initialization.
     * 
     * @param intersections
     *            The number of intersections in which the crossover will swap
     *            segments between parents.
     */
    public void setNumberOfIntersections(int intersections) {
        this.numberOfIntersections = intersections;
    }

    /**
     * Cross mutates between the parents at points with fixed interval. The
     * final Song will have segments randomly distributed from the parents.
     * 
     * @return The resulting Song object of the cross mutation.
     */
    public Song makeCrossover(List<Individual> parents) {
        for (TrackTag t: getCommonTracks(parents)){
            System.out.println(t.toString());
        }
        Map<Individual, Phrase[]> phraseSections = new HashMap<Individual, Phrase[]>();
        for (int i = 0; i < parents.size(); i++) {
            phraseSections.put(parents.get(i), getPhraseIntersections(parents
                    .get(i).getSong().getTrack(0).getPhrase(0)));
        }

        Phrase sumPhrase = new Phrase();
        int nextRandom;
        for (int i = 0; i < numberOfIntersections; i++) {
            nextRandom = (int) (randomGen.nextDouble() * parents.size());
            sumPhrase
                    .addNoteList(phraseSections.get(parents.get(nextRandom))[i]
                            .getNoteArray());
        }

        double averageTempo = 0;
        for (int i = 0; i < parents.size(); i++) {
            averageTempo += parents.get(i).getSong().getTempo();
        }
        averageTempo = averageTempo / parents.size();

        Score finalScore = new Score(new Part(sumPhrase),
                "Generated from crossover", averageTempo);

        return new Song(finalScore);
    }
    
    /**
     * Returns a list of all common tags that all individuals have
     * 
     * @param parents
     * @return
     */
    private List<TrackTag> getCommonTracks(List<Individual> parents){
        List<TrackTag> commonTrackTags = new ArrayList<TrackTag>();
        TrackTag[] tracks = TrackTag.values();
        
        for (TrackTag tag : tracks){
            commonTrackTags.add(tag);
            for (Individual i : parents){
               if (i.getSong().getTaggedTracks(tag).isEmpty()){
                   commonTrackTags.remove(tag);
               }
            }
        }
        return commonTrackTags;
    }

    /**
     * Gets a Phrase chopped up in Phrases at even intervals determined by the
     * set number of intersections. The source phrase will not be modified.
     * 
     * @param sourcePhrase
     *            The Phrase that will be chopped up.
     * @return An array containing the given Phrase chopped up in smaller
     *         phrases.
     */
    private Phrase[] getPhraseIntersections(Phrase sourcePhrase) {
        double endTime = sourcePhrase.getEndTime();
        double phraseTime = endTime / numberOfIntersections;
        Phrase[] intersections = new Phrase[numberOfIntersections];

        for (int i = 0; i < numberOfIntersections; i++) {
            intersections[i] = sourcePhrase.copy(i * phraseTime, (i + 1)
                    * phraseTime);
        }

        return intersections;

    }

}
