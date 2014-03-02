package mutation;

public class MidiUtil {
    public final static int NBR_OF_NOTES = 12;
    private String[] noteIndex = {"C", "Cm", "D", "Dm", "E", "F", "Fm", "G", "Gm", "A", "Am", "B"};

    public MidiUtil() {
    }

    public int getNotePitch(int midiNbr){
        return ((midiNbr / 12) - 1);
    }
    
    public String getNoteName(int midiNbr){
        return noteIndex[(midiNbr%12)];
    }
}
