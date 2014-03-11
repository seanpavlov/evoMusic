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
    private int simplicity = 10;
    private int silenceduration = 20;
    private Random randomGen;

    public Crossover(int intersections){
        randomGen = new Random();
        this.intersections = intersections;
    }

    public void setSimplicityLevel(int simplicity){
        this.simplicity = simplicity;
    }
    
    public void setMaxSilenceDiration(int silenceduration){
        this.silenceduration = silenceduration;
    }
    
    
    public Song makeCrossover(List<Individual> parents){
        Score finalScore = new Score();
        Song child = new Song(finalScore);
        List<List<Part>> tracksWithTag = new ArrayList<List<Part>>();
        double averageTempo = 0;
        
        List<TrackTag> tags = getCommonTracks(parents);
        if (tags.isEmpty()){
            System.err.println("No common tracktags, parents could not be crossed");
            return new Song(new Score("empty song"));
        } else {
            for (TrackTag t : tags){
                averageTempo = 0;
                for (Individual i : parents){
                    averageTempo += i.getSong().getTempo();
                    List<Part> p = i.getSong().getTaggedTracks(t);
                    tracksWithTag.add(i.getSong().getTaggedTracks(t));
                }
                
                Part newTagPart = crossTaggedTracks(tracksWithTag);
                finalScore.add(newTagPart);
                
                tracksWithTag.clear();
            }
            
            averageTempo = averageTempo / parents.size();
            finalScore.setTempo(averageTempo);
        }
        
        return child;
    }
    
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
                }
            }
        }

        return newTagPart;
    }

    private List<Phrase> morph(List<Phrase> choppedTrack) {
        List<Phrase> newPhrases = new ArrayList<Phrase>(); 
        for (int i = 0; i < choppedTrack.size() - intersections; i+=intersections){
            Phrase newPhrase = choppedTrack.get(randomGen.nextInt(intersections) + i);   
            newPhrases.add(newPhrase);
        }        
        
        return newPhrases;
    }

    private List<Phrase> chop(Part taggedTrackPart) {
        List<Phrase> choppedPhrases = new ArrayList<Phrase>();
        
        for (Phrase phrase : taggedTrackPart.getPhraseArray()){
            Note[] phraseNotes = phrase.getNoteArray();
            int numberOfNotes = phraseNotes.length / intersections;
            Phrase newPhrase = new Phrase();
            if (numberOfNotes > 1 ){
                for (int i = 0; i < phraseNotes.length; i++){
                    if (phraseNotes[i].getDuration() < silenceduration) newPhrase.addNote(phraseNotes[i]);
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
