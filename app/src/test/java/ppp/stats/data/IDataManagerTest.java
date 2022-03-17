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
        assertEquals(1, users.size());
        UserModel user = users.get(Long.valueOf(1));
        assertEquals(1, user.getId());
        assertEquals("Alice", user.getName());

        this.testDataManager.setUserName(1, "Bob");
        users = this.testDataManager.getUserModels();
        assertEquals(1, users.size());
        user = users.get(Long.valueOf(1));
        assertEquals(1, user.getId());
        assertEquals("Bob", user.getName());

        this.testDataManager.setUserName(2, "Charlie");
        users = this.testDataManager.getUserModels();
        assertEquals(2, users.size());
        user = users.get(Long.valueOf(1));
        assertEquals(1, user.getId());
        assertEquals("Bob", user.getName());
        user = users.get(Long.valueOf(2));
        assertEquals(2, user.getId());
        assertEquals("Charlie", user.getName());
    }

    @Test
    public void testAddUserTime() {
        long userId = 1;
        this.testDataManager.setUserName(userId, "Alice");

        LocalDate today = IChannelDataManager.MiniDate();
        this.testDataManager.addUserTime(userId, 7, 0);
        var times = this.testDataManager.getTimesForUserId(userId);
        assertEquals(1, times.size());
        assertEquals(7, times.get(today).getTime());
        assertEquals(0, times.get(today).getMessageId().longValue());

        LocalDate tomorrow = today.plusDays(1);
        this.testDataManager.addUserTime(userId, tomorrow, 8, 1);
        times = this.testDataManager.getTimesForUserId(userId);
        assertEquals(2, times.size());
        assertEquals(7, times.get(today).getTime());
        assertEquals(0, times.get(today).getMessageId().longValue());
        assertEquals(8, times.get(tomorrow).getTime());
        assertEquals(1, times.get(tomorrow).getMessageId().longValue());
    }

    @Test
    public void testGetTimesForDate() {
        this.testDataManager.setUserName(1, "Alice");
        this.testDataManager.setUserName(2, "Bob");

        LocalDate today = IChannelDataManager.MiniDate();
        this.testDataManager.addUserTime(1, 7, 1);
        this.testDataManager.addUserTime(2, 8, 2);
        var results = this.testDataManager.getTimesForDate(today);
        assertEquals(2, results.size());
        assertEquals(7, results.get(Long.valueOf(1)).getTime());
        assertEquals(1, results.get(Long.valueOf(1)).getMessageId().longValue());
        assertEquals(8, results.get(Long.valueOf(2)).getTime());
        assertEquals(2, results.get(Long.valueOf(2)).getMessageId().longValue());

        LocalDate tomorrow = today.plusDays(1);
        this.testDataManager.addUserTime(1, tomorrow, 9, 3);
        this.testDataManager.addUserTime(2, tomorrow, 10, 4);
        results = this.testDataManager.getTimesForDate(tomorrow);
        assertEquals(2, results.size());
        assertEquals(9, results.get(Long.valueOf(1)).getTime());
        assertEquals(3, results.get(Long.valueOf(1)).getMessageId().longValue());
        assertEquals(10, results.get(Long.valueOf(2)).getTime());
        assertEquals(4, results.get(Long.valueOf(2)).getMessageId().longValue());
    }
}
