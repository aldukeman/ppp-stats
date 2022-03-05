package ppp.stats.data;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import junit.framework.TestCase;

import ppp.stats.data.model.UserModel;

public abstract class IDataManagerTest extends TestCase {
    protected IChannelDataManager testDataManager;

    @Test
    public void testSetUserName() {
        this.testDataManager.setUserName(1, "Alice");
        Map<Long, UserModel> users = this.testDataManager.getUserModels();
        assertEquals(users.size(), 1);
        UserModel user = users.get(Long.valueOf(1));
        assertEquals(user.getId(), 1);
        assertEquals(user.getName(), "Alice");

        this.testDataManager.setUserName(1, "Bob");
        users = this.testDataManager.getUserModels();
        assertEquals(users.size(), 1);
        user = users.get(Long.valueOf(1));
        assertEquals(user.getId(), 1);
        assertEquals(user.getName(), "Bob");

        this.testDataManager.setUserName(2, "Charlie");
        users = this.testDataManager.getUserModels();
        assertEquals(users.size(), 2);
        user = users.get(Long.valueOf(1));
        assertEquals(user.getId(), 1);
        assertEquals(user.getName(), "Bob");
        user = users.get(Long.valueOf(2));
        assertEquals(user.getId(), 2);
        assertEquals(user.getName(), "Charlie");
    }

    @Test
    public void testAddUserTime() {
        long id = 1;
        this.testDataManager.setUserName(id, "Alice");

        LocalDate today = IChannelDataManager.MiniDate();
        this.testDataManager.addUserTime(id, 7);
        Map<LocalDate, Integer> times = this.testDataManager.getTimesForUserId(id);
        assertEquals(times.size(), 1);
        assertEquals(times.get(today), Integer.valueOf(7));

        LocalDate tomorrow = today.plusDays(1);
        this.testDataManager.addUserTime(id, tomorrow, 8);
        times = this.testDataManager.getTimesForUserId(id);
        assertEquals(times.size(), 2);
        assertEquals(times.get(today), Integer.valueOf(7));
        assertEquals(times.get(tomorrow), Integer.valueOf(8));
    }

    @Test
    public void testGetTimesForDate() {
        this.testDataManager.setUserName(1, "Alice");
        this.testDataManager.setUserName(2, "Bob");

        LocalDate today = IChannelDataManager.MiniDate();
        this.testDataManager.addUserTime(1, 7);
        this.testDataManager.addUserTime(2, 8);
        Map<Long, Integer> results = this.testDataManager.getTimesForDate(today);
        assertEquals(results.size(), 2);
        assertEquals(results.get(Long.valueOf(1)), Integer.valueOf(7));
        assertEquals(results.get(Long.valueOf(2)), Integer.valueOf(8));

        LocalDate tomorrow = today.plusDays(1);
        this.testDataManager.addUserTime(1, tomorrow, 9);
        this.testDataManager.addUserTime(2, tomorrow, 10);
        results = this.testDataManager.getTimesForDate(tomorrow);
        assertEquals(results.size(), 2);
        assertEquals(results.get(Long.valueOf(1)), Integer.valueOf(9));
        assertEquals(results.get(Long.valueOf(2)), Integer.valueOf(10));
    }
}
