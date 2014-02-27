package com.evoMusic.model.enumerators;

public enum TrackTag {
	MELODY, CHORDS,
	RHYTHM, BEAT,
	DRUMS,   NONE;
	
	public static boolean contains(String tag){
	    for (TrackTag t : TrackTag.values()){
	        if (t.toString().equals(tag.toUpperCase()))
	            return true;
	    }
	    return false;
	}
}
