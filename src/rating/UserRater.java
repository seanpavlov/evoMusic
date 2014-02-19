package rating;

import java.util.Scanner;

import translator.Translator;
import model.Song;

public class UserRater implements ISubRater {
    private double weight;
    
    /**
     * Constructor, creates a user rater
     * 
     * @param weight
     */
    public UserRater(double weight){
        setWeight(weight);
    }
    
    @Override
    public double rate(Song song) {
        double rating = 0.0;
        Translator.INSTANCE.playSong(song);
        Scanner sc = new Scanner(System.in);
        System.out.println("Waiting for user rating input : ");
        rating = sc.nextDouble();
        return rating * weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double weight) {
        if (weight >= 0 && weight <= 1){
            this.weight = weight;
        } else {
            System.err.println("Weight must be between 0 and 1");
        }
    }

    @Override
    public boolean shouldRate() {
        return (weight!=0.0);
    }
}
