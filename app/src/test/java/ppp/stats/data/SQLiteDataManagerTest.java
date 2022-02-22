package ppp.stats.data;

import org.junit.Test;

import junit.framework.TestCase;
import ppp.stats.data.model.UserModel;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public class SQLiteDataManagerTest extends TestCase {
    private static ILogger logger = new SystemOutLogger();
    private SQLiteDataManager testDataManager;

    protected void setUp() {
        File test = new File(this.getName());
        test.delete();
        this.testDataManager = new SQLiteDataManager(this.getName(), SQLiteDataManagerTest.logger);
    }
    
    protected void tearDown() {
        File test = new File(this.getName());
        test.delete();
    }

    private LocalDate miniDate() {
        return LocalDate.now(ZoneId.of("America/New_York"));
    }

    @Test
    public void testSetUserName() {
        this.testDataManager.setUserName(1, "Alice");
        List<UserModel> users = this.testDataManager.getUserModels();
        assertTrue(users.size() == 1);
        UserModel user = users.get(0);
        assertTrue(user.getId() == 1);
        assertEquals(user.getName(), "Alice");

        this.testDataManager.setUserName(1, "Bob");
        users = this.testDataManager.getUserModels();
        assertTrue(users.size() == 1);
        user = users.get(0);
        assertTrue(user.getId() == 1);
        assertEquals(user.getName(), "Bob");
    }

    @Test
    public void testAddUserTime() {
        this.testDataManager.setUserName(1, "Alice");

        this.testDataManager.addUserTime(1, 7);
        UserTimesDictionary times = this.testDataManager.getTimesForUserId(1);
        assertTrue(times.size() == 1);
        Map.Entry<LocalDate, Integer> entry = times.entrySet().iterator().next();
        assertEquals(entry.getKey(), this.miniDate());
        assertEquals(entry.getValue(), Integer.valueOf(7));
    }

    @Test
    public void testGetTimesForDate() {
        this.testDataManager.setUserName(1, "Alice");
        this.testDataManager.setUserName(2, "Bob");

        this.testDataManager.addUserTime(1, 7);
        this.testDataManager.addUserTime(2, 8);
        Map<Long, Integer> results = this.testDataManager.getTimesForDate(this.miniDate());
        assertTrue(results.size() == 2);
        assertEquals(results.get(Long.valueOf(1)), Integer.valueOf(7));
        assertEquals(results.get(Long.valueOf(2)), Integer.valueOf(8));
    }
}