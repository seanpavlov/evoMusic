package translator;

import structure.Song;

public interface ITranslator {
	
    /**
     * Loads a MIDI file and call the constructor of song object
     * on load success
     * 
     * @param path, relative path to MIDI file
     */
	public Song loadMidi(String path);
	
	
    /**
     * Function takes a path to save the MIDI file to, and the object which 
     * to save and saves it.
     * 
     * @param path, where to save
     * @param song, song object to unload
     */
    public void saveMidi(String path, Song song);
}
