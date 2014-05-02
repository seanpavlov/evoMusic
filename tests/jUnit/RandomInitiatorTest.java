package jUnit;

import org.junit.Before;
import org.junit.Test;
import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.RandomInitiator;

public class RandomInitiatorTest {
    RandomInitiator ri;
    
    @Before
    public void setUp() {
        ri = new RandomInitiator(120);
    }    
    
    @Test
    public void test() {
        Song s = ri.generateMelody();
        Translator.INSTANCE.show(s);
        Translator.INSTANCE.play(s);
    }

}
