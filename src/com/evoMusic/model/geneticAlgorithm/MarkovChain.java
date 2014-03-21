package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;

public class MarkovChain {
	
	List<int[]> originalIntervals;
	
	public MarkovChain(Song song) {
		originalIntervals = new ArrayList<int[]>();
		for(Part part : song.getScore().getPartArray()) {
			for(Phrase phrase : part.getPhraseArray()) {
				for(Note note : phrase.getNoteArray()) {
					note.get
				}
			}
		}
	}

}
