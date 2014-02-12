package translator;

import structure.Song;

public interface ITranslator {
	
	
    /**
     * Loads a MIDI file and call the constructor of song object
     * on load success
     * 
     * @param path, relative path to MIDI file
     */
	public abstract Song loadMidiToSong(String path);
	
	
    /**
     * Function takes a path to save the MIDI file to, and the object which 
     * to save and saves it.
     * 
     * @param path, where to save
     * @param name, name of the file
     * @param song, song object to unload
     * 
     */
    public void saveSongToMidi(String path, String name, Song song);
    
}
