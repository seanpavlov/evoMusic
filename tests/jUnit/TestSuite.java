package jUnit;

import jUnit.database.MongoDatabaseTest;
import jUnit.translator.TranslatorTest;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.evoMusic.database.MongoDatabase;;


@RunWith(Suite.class)
@SuiteClasses({TranslatorTest.class, GenerationTest.class, MongoDatabaseTest.class })

/**
 * This is our test suite class. This class runs all our test classes. 
 * To get started with jUnit 4.x, here's a good article: 
 * http://www.vogella.com/tutorials/JUnit/article.html especially sections 2.2, 
 * 3.1, 3.2, 3.3, 5.5.
 * 
 */
public class TestSuite {

    public final static String TEST_DB = MongoDatabase.DB_NAME + "_TEST";
    
    /**
     * Set up Mongo to use another database name to keep the default one clean
     * If a mongo db has not been installed or set up properly, we get to
     * know about this before any tests are run. 
     */
    @BeforeClass
    public static void setUpDb() {
        MongoDatabase.getInstance().dropDb(TEST_DB);
        MongoDatabase.getInstance().useDbName(TEST_DB);
    }
}
