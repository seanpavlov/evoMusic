package main;
import jm.JMC;
import jm.music.data.*;
import jm.util.Play;

public class Main implements JMC{
	
	public static void main(String[] args) {
		System.out.println("Wow. Such music program. Much code.");
		Play.midi(new Note(C4, QN));
		Play.midi(new Note(C4, QN));
		Play.midi(new Note(D4, QN));
		Play.midi(new Note(D4, QN));
		Play.midi(new Note(F4, QN));

	}

}
