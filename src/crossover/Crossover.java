package crossover;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import model.Song;

public class Crossover {

    private Song[] parents;
    private int numberOfIntersections;
    Random randomGen;

    /**
     * Creates a mutable Crossover object containing parents that are to be
     * crossed. The parents will not be modified by this class.
     * 
     * @param parents
     *            The Song objects that will be used during crossover.
     */
    public Crossover(Song[] parents) {
        this.parents = parents;
        this.numberOfIntersections = parents.length;
        randomGen = new Random();
    }

    /**
     * Sets the number of intersections in which the crossover will swap
     * segments between parents. The default value is the number of parents
     * during initialization.
     * 
     * @param intersections
     *            The number of intersections in which the crossover will swap
     *            segments between parents.
     */
    public void setNumberOfIntersections(int intersections) {
        this.numberOfIntersections = intersections;
    }

    /**
     * Cross mutates between the parents at points with fixed interval. The
     * final Song will have segments randomly distributed from the parents.
     * 
     * @return The resulting Song object of the cross mutation.
     */
    public Song crossMutate() {

        Map<Song, Phrase[]> phraseSections = new HashMap<Song, Phrase[]>();
        for (int i = 0; i < parents.length; i++) {
            phraseSections.put(parents[i], getPhraseIntersections(parents[i]
                    .getPart(0).getPhrase(0)));
        }

        Phrase sumPhrase = new Phrase();
        int nextRandom;
        for (int i = 0; i < numberOfIntersections; i++) {
            nextRandom = (int) (randomGen.nextDouble() * parents.length);
            sumPhrase.addNoteList(phraseSections.get(parents[nextRandom])[i]
                    .getNoteArray());
        }

        double averageTempo = 0;
        for (int i = 0; i < parents.length; i++) {
            averageTempo += parents[i].getTempo();
        }
        averageTempo = averageTempo / parents.length;

        Score finalScore = new Score(new Part(sumPhrase),
                "Generated from crossover", averageTempo);

        return new Song(finalScore, finalScore.getTitle());
    }

    /**
     * Sets the active parents that will be used during crossover. The old
     * parents will no longer be used by this class.
     * 
     * @param parents
     *            The Song parents that are to be used in the cross mutation.
     */
    public void setParents(Song[] parents) {
        this.parents = parents;
    }

    /**
     * Gets a Phrase chopped up in Phrases at even intervals determined by the
     * set number of intersections. The source phrase will not be modified.
     * 
     * @param sourcePhrase
     *            The Phrase that will be chopped up.
     * @return An array containing the given Phrase chopped up in smaller
     *         phrases.
     */
    private Phrase[] getPhraseIntersections(Phrase sourcePhrase) {
        double endTime = sourcePhrase.getEndTime();
        double phraseTime = endTime / numberOfIntersections;
        Phrase[] intersections = new Phrase[numberOfIntersections];

        for (int i = 0; i < numberOfIntersections; i++) {
            intersections[i] = sourcePhrase.copy(i * phraseTime, (i + 1)
                    * phraseTime);
        }

        return intersections;

    }

}
