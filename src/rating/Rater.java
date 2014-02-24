package rating;

import java.util.ArrayList;
import java.util.List;

import model.Song;

public class Rater {
    List<ISubRater> subraters = new ArrayList<ISubRater>();

    /**
     * Creates a new Rater with no subraters.
     */
    public Rater() {
    }

    /**
     * Creates a new Rater with a given list of ISubRater.
     * 
     * @param subraters
     *            The given list of ISubRater.
     */
    public Rater(List<ISubRater> subraters) {
        this.addSubRater(subraters);
    }

    /**
     * rates a song with the help of all subraters in the list
     * 
     * @param song
     *            to be rater
     * @return combined rating of the song
     */
    public double rate(Song song) {

        double rating = 0.0;
        double weightAvg = 0.0;
        for (ISubRater subRater : subraters) {
            weightAvg += subRater.getWeight();
            if (subRater.shouldRate()) {
                rating += subRater.rate(song) * subRater.getWeight();
            }
        }
        rating = rating / subraters.size();
        weightAvg = weightAvg / subraters.size();
        rating = rating / weightAvg;
        return rating;
    }

    /**
     * Adds a subrater to this rater's list of raters.
     * 
     * @param subRater
     *            The ISubRater to be added.
     */
    public void addSubRater(ISubRater subRater) {
        this.subraters.add(subRater);
    }

    /**
     * Adds a list of subraters to this rater's list of subraters.
     * 
     * @param subraters
     *            The list of ISubRater to be added.
     */
    public void addSubRater(List<ISubRater> subraters) {
        this.subraters.addAll(subraters);
    }

    /**
     * Takes a list of songs and initiate the raters weights depending on the
     * rating of each song.
     * 
     * @param songs
     *            songs to initiate the rating
     */
    public void initSubRaterWeights(Song[] songs) {
        double rating;
        for (ISubRater subRater : subraters) {
            rating = 0;
            for (Song s : songs) {
                rating += subRater.rate(s);
            }
            rating = rating / songs.length;
            subRater.setWeight(rating);
        }
    }
}