package com.evoMusic.util;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Phrase;

import com.evoMusic.model.Song;

public enum Sort  {
    INSTANCE;
    
    /**
     * Sorts all phrases in a song on their startTime 
     * and returns the sorted array of phrases
     * 
     * @param song
     * @return
     */
    public List<Phrase> sortPhrasesOnStartTime(Song song){
        List<Phrase> allPhrases = new ArrayList<Phrase>();
         
         for (int i = 0; i < song.getNbrOfTracks(); i++){ 
             for (Phrase phrase : song.getTrack(i).getPhraseArray()){
                 if (allPhrases.isEmpty()){
                     allPhrases.add(phrase);
                 } else {
                     boolean added = false;
                     for (int j = 0; j < allPhrases.size(); j++){
                         if (allPhrases.get(j).getStartTime() >= phrase.getStartTime()){
                             allPhrases.add(j, phrase);
                             added = true;
                             break;
                         }
                     }
                     if (!added){
                         allPhrases.add(phrase);
                     }
                 }
             }
         }
         return allPhrases;
     }

    /**
     * Sorts all phrases in a song on their endTime 
     * and returns the sorted array of phrases
     * 
     * @param song
     * @return
     */
    public List<Phrase> sortPhrasesOnEndTime(Song song){
        List<Phrase> allPhrases = new ArrayList<Phrase>();
         
         for (int i = 0; i < song.getNbrOfTracks(); i++){ 
             for (Phrase phrase : song.getTrack(i).getPhraseArray()){
                 if (allPhrases.isEmpty()){
                     allPhrases.add(phrase);
                 } else {
                     boolean added = false;
                     for (int j = 0; j < allPhrases.size(); j++){
                         if (allPhrases.get(j).getEndTime() >= phrase.getEndTime()){
                             allPhrases.add(j, phrase);
                             added = true;
                             break;
                         }
                     }
                     if (!added){
                         allPhrases.add(phrase);
                     }
                 }
             }
         }
         return allPhrases;
     }

}
