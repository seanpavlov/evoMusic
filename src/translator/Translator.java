package translator;

import jm.music.data.Score;
import jm.util.Read;
import structure.Song;

public class Translator implements ITranslator {
    
    public Song loadMidi(String path){
        Score score = new Score("score1");
        Read.midi(score, path);
        return new Song(score);
    }
 
    
    public void saveMidi(String path, Song song){
        //must know more about object
        
    }
    
    public void play(Object song){
        //Play.midi(song.getScore());
          
    }
    
}
