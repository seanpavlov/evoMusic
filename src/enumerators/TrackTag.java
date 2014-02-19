package enumerators;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum TrackTag implements Serializable {
	MELODY("melody"), CHORDS("chords"), 
	RHYTHM("rhythm"), BEAT("beat"), 
	DRUMS("drums"),   NONE("none");
	
	/**
	 * dbName is the enum representation used in mongo. 
	 * This is because enums are not yet supported by the driver
	 */
    public final String dbName;
	private final static Map<String, TrackTag> tagMap = new HashMap<String, TrackTag>();
    
	private TrackTag(String dbName){
	    this.dbName = dbName;
	    getTagmap().put(dbName, this);
	}
	
	public static TrackTag getTag(String dbName) {
	    return getTagmap().get(dbName);
	}

    private static Map<String, TrackTag> getTagmap() {
        return tagMap;
    }
	
}
