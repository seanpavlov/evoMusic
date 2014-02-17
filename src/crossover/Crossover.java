package crossover;

import model.Song;

public class Crossover {
    
    private Song[] parents;
    
    public Crossover (Song[] parents) {
        this.parents = parents;
    }
    
    public Song crossMutate() {
        return null;
    }
    
    public void setParents(Song[] parents) {
        this.parents = parents;
    }
    
}
