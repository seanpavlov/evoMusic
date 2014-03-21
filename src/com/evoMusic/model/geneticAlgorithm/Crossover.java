package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import com.evoMusic.model.Song;
import com.evoMusic.util.TrackTag;

public class Crossover {
    private int intersections;
    private Integer maxDuration = null;
    private Integer minDuration = 0;

    private int silenceLength = 40;
    private int simplicity = 5;
    private Random randomGen;    

    /**
     * Constructor for crossover
     * 
     * @param intersections
     */
    public Crossover(int intersections){
        randomGen = new Random();
        this.intersections = intersections;
    }

    /**
     * Sets the maximum length of the results, defaults to no restriction if 
     * not set specifically with this method.
     * 
     * @param maxDuration
     */
    public void setMaxDuration(int maxDuration){
        this.maxDuration = maxDuration;
    }
    
    /**
     * Sets the minimum length of the results, defaults to 10
     * high values increases time consumption drastically
     * 
     * @param maxDuration
     */
    public void setMinDuration(int minDuration){
        this.minDuration = minDuration;
    }
    
    /**
     * Set simplicity level, the higher the value the less complex the results will be,
     * defaults to 1 (complex) and should not exceed 100.
     * 
     * @param simplicity
     */
    private void setSimplicityLevel(int simplicity){
        this.simplicity = simplicity;
    }
    
    /**
     * Set maximum silence duration in a song, defaults to 20.
     * 
     * @param silenceLength
     */
    private void setMaxSilenceLength(int silenceLength){
        this.silenceLength = silenceLength;
    }
    
    /**
     * Called to generate one new offspring from a list of parents
     * 
     * @param parents
     * @return child
     */
    public Song makeCrossover(List<Individual> parents){
        this.setMaxSilenceLength(randomGen.nextInt(40)+1);
        this.setSimplicityLevel(randomGen.nextInt(100)+2);
        
        Score finalScore = new Score();
        Song child = new Song(finalScore);
        List<List<Part>> tracksWithTag = new ArrayList<List<Part>>();
        double averageTempo = 0;
        
        List<TrackTag> tags = getCommonTracks(parents);
        tags.remove(TrackTag.NONE);
        if (tags.isEmpty()){
            try {
                throw new Exception("No common tracktags, parents could not be crossed");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            for (TrackTag t : tags){
                averageTempo = 0;
                for (Individual i : parents){
                    averageTempo += i.getSong().getTempo();
                    tracksWithTag.add(i.getSong().getTaggedTracks(t));
                }
                Part newTagPart = crossTaggedTracks(tracksWithTag);
                finalScore.add(newTagPart);
                child.addTagToTrack(newTagPart, t);
                
                tracksWithTag.clear();
            }
            
            averageTempo = averageTempo / parents.size();
            finalScore.setTempo(averageTempo);
        }
        
        // obs, recursive call, forces results to be longer than a minimal duration, 
        // increasing minDuration drastically increases complexity
        if (child.getScore().getEndTime() < minDuration) return makeCrossover(parents);
        return child;
    }
    
    /**
     * Called for every common track tag found in the list of parents, results in one new track,
     * for example melody or beat. 
     * 
     * @param tracksWithTag
     * @return
     */
    private Part crossTaggedTracks(List<List<Part>> tracksWithTag) {
        Part newTagPart = new Part();
        //loops all tracks with one tracktag in all parents
        for (List<Part> taggedTracksInParent: tracksWithTag){
            
            //loops all tracks in one parent with same tracktag
            for (Part taggedTrackPart: taggedTracksInParent){        
                List<Phrase> choppedTrack = chop(taggedTrackPart);
                List<Phrase> morphedTracks = morph(choppedTrack);
                for (Phrase tempPhrase : morphedTracks){
//                  tempPhrase.setInstrument(taggedTrackPart.getInstrument());
                    newTagPart.add(tempPhrase);
                    if (maxDuration != null && newTagPart.getEndTime() > maxDuration){ 
                        newTagPart.removeLastPhrase();
                        return newTagPart;
                    };
                }
            }
        }
        return newTagPart;
    }

    /**
     * Chops a part into sections depending on number of intersections, each sub part contains equally
     * many notes.
     * 
     * @param taggedTrackPart
     * @return
     */
    private List<Phrase> chop(Part taggedTrackPart) {
        List<Phrase> choppedPhrases = new ArrayList<Phrase>();
        
        for (Phrase phrase : taggedTrackPart.getPhraseArray()){
            Note[] phraseNotes = phrase.getNoteArray();
            int numberOfNotes = phraseNotes.length / intersections;
            Phrase newPhrase = new Phrase();
            if (numberOfNotes > 1 ){
                for (int i = 0; i < phraseNotes.length; i++){
                    phraseNotes[i].setPan(0.5);
                    phraseNotes[i].setDynamic(64);
                    
                    if (phraseNotes[i].getDuration() < silenceLength) newPhrase.addNote(phraseNotes[i]);
                    if (i % numberOfNotes == 0 && newPhrase.length() > simplicity){
                        choppedPhrases.add(newPhrase);
                        newPhrase = new Phrase();
                    }
                }
            }
        }
        return choppedPhrases;
    }
    
    /**
     * Combines a list of phrases into a new list, this list has randomized parts
     * from the parameter list
     * 
     * @param choppedTrack
     * @return
     */
    private List<Phrase> morph(List<Phrase> choppedTrack) {
        List<Phrase> newPhrases = new ArrayList<Phrase>();
        for (int i = 0; i < choppedTrack.size(); i++){
            Phrase newPhrase = choppedTrack.get(randomGen.nextInt(choppedTrack.size()));
            newPhrases.add(newPhrase);
        }
        
        return newPhrases;
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
                   break;
               }
            }
        }
        return commonTrackTags;
    }
}
