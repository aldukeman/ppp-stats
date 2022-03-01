package ppp.stats.data;

import ppp.stats.logging.NoOpLogger;

import java.io.File;
import java.sql.SQLException;

public class SQLiteDataManagerTest extends IDataManagerTest {
    protected void setUp() throws SQLException {
        File test = new File(this.getName());
        test.delete();
        this.testDataManager = new SQLiteDataManager(this.getName(), new NoOpLogger());
    }

    protected void tearDown() {
        File test = new File(this.getName());
        test.delete();
    }
}