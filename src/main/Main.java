package main;

import jm.JMC;
import jm.music.data.*;
import jm.util.Play;
import jm.util.Read;
import jm.util.View;

public class Main implements JMC{
            
    public static void main(String[] args) {
		Score sweden = new Score("sweden");
		Read.midi(sweden, "/home/oted/workspace/evoMusic/midifiles/Sweden.mid");
		
		Score norway = new Score("norway");
        Read.midi(norway, "/home/oted/workspace/evoMusic/midifiles/Norway.mid");
        
		
        Part[] s = sweden.getPartArray();
        Part[] n = norway.getPartArray();

        Score comb = new Score("nosw");
        
        n[1].setTempo(s[1].getTempo());
        
        comb.addPartList(n);
        comb.addPartList(s);
        
        Play.midi(comb);
     
    }

}
