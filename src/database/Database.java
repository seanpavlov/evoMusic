package database;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import model.Song;

public class Database implements IDatabase{
    
    private static Database instance = null;
    private MongoClient mongoClient;
    private DB db;
    
    protected Database(){
        try {
            mongoClient = new MongoClient();
            db = this.mongoClient.getDB("evoMusic");
        } catch (UnknownHostException e) {
            //Take care of Exception TODO
            e.printStackTrace();
        }
    }
    
    public static Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    @Override
    public BasicDBObject createDBObject(Song song) {
        //TODO implement method
        return null;
    }

    @Override
    public Song createSongObject(BasicDBObject dbDoc) {
        //TODO implement method
        return null;
    }

    @Override
    public void saveDbObject(BasicDBObject dbDoc) { 
        //TODO implement method
    }

    @Override
    public BasicDBObject retreiveDBObject() {
        //TODO implement method
        return null;
    }
    
}
