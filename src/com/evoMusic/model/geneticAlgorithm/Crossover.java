package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
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
        int c = 0;
        for (Individual i : parents){
            for (Part p : i.getSong().getScore().getPartArray()){
                if (c % 3 == 0){ 
                    i.getSong().addTagToTrack(p, TrackTag.BEAT);
                } else if (c % 5 == 0) {
                    i.getSong().addTagToTrack(p, TrackTag.MELODY);  
                } else {
                    i.getSong().addTagToTrack(p, TrackTag.NONE);  
                }
                c++;
            }
        }

        List<TrackTag> tags = getCommonTracks(parents);
        if (tags.isEmpty()){ 
            //if theres no common tracks, watdo?
            System.err.println("No common tracktags, parents could not be crossed");
            return new Song(new Score("empty song"));
        } else {
            List<List<Part>> tracksWithTag = new ArrayList<List<Part>>();
            
            Score finalScore = new Score();
            Song finalSong = new Song(finalScore);
            int counter = 0;
            double averageTempo = 0;
            
            // tags now contains all common tags, loop over all of them and send them to crossTags one by one
            for (TrackTag t : tags){
                for (Individual i : parents){
                    tracksWithTag.add(i.getSong().getTaggedTracks(t));
                    System.out.println(tracksWithTag.size());
                }
                
                finalScore.add(crossTracks(tracksWithTag, t));
                finalSong.addTagToTrack(counter, t);
                tracksWithTag.clear();
                counter++;
            }

            for (Individual i : parents){
                averageTempo += i.getSong().getTempo();
            }

            averageTempo = averageTempo / parents.size();
            finalScore.setTempo(averageTempo);
            
    
            return finalSong;
        }
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
                   //break??
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
    /**
     * Merge two or more parts into one single phrase
     * 
     * @param parts
     * @return
     */
    private Part mergeTracks(List<Part> parts){
        Part newPart = new Part();
        List<Phrase[]> newPhrases = new ArrayList<Phrase[]>();

        for (Part p : parts){
            Phrase onePhrasePart = mergePhrases(p);
            newPhrases.add(getPhraseIntersections(onePhrasePart));
        }
        
        int nextRandom;
        for (int i = 0; i < numberOfIntersections; i++) {
            nextRandom = (int) (randomGen.nextDouble() * newPhrases.size());
            newPart.addPhraseList(newPhrases.get(nextRandom));
        }

        return newPart;
    }
    
    /**
     * Merge all tracks form all individuals with the same TrackTag,
     *  
     * 
     * @param tracksWithTag, all individuals mapped to their Tracks with Tag 
     * @param tag, the current tag
     * @return a new Part where all tracks are join
     */
    private Part crossTracks(List<List<Part>> tracksWithTag, TrackTag tag){
        Part p = new Part();
        List<Part> newParts = new ArrayList<Part>();

        for (int i = 0; i < tracksWithTag.size(); i++) {
            newParts.add(mergeTracks(tracksWithTag.get(i)));
        }
        
        p = mergeTracks(newParts);
        
        return p;
    }

    /**
     * Merge many parts to one
     * 
     * @param parts
     * @return
     */
    private Phrase mergePhrases(Part part){
        Phrase phrase = new Phrase();
        for (Phrase tempPhrase : part.getPhraseArray()){
            phrase.addNoteList(tempPhrase.getNoteArray());
        }
        
        return phrase;
    }
    
}
