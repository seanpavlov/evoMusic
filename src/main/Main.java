package main;
import jm.JMC;
import jm.music.data.*;
import jm.util.Play;
import jm.util.Read;
import jm.util.View;

public class Main implements JMC{
            
    public static void main(String[] args) {
		System.out.println("Wow. Such music program. Much code.");
		Score score = new Score("m83");
		Read.midi(score, "/home/oted/workspace/evoMusic/midifiles/m83-midnight_city.mid");
		Part score2 = score.getPart(1);
		System.out.println(score2.getTitle());
		Play.midi(score2);
    }

}
