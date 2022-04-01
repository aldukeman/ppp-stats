package ppp.stats.data;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import ppp.stats.data.model.UserModel;
import ppp.stats.data.model.WordleResultModel;

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

    @Test
    public void testWordleResultsForUser() {
        this.testDataManager.setUserName(1, "Alice");
        this.testDataManager.setUserName(2, "Bob");
        List<String> tests = List.of("11111 22222 33333", "11311 22311 33322 33333");

        for (int i = 0; i < tests.size(); ++i) {
            String aRes = tests.get(i);
            String bRes = tests.get(tests.size() - 1 - i);

            WordleResultModel aModel = WordleResultModel.from(aRes, i % 2 == 0);
            WordleResultModel bModel = WordleResultModel.from(bRes, i % 2 == 1);

            LocalDate d = LocalDate.now().plusDays(i);
            this.testDataManager.addWordleResult(1, d, aModel, i * 2);
            this.testDataManager.addWordleResult(2, d, bModel, i * 2 + 1);
        }

        for (int i = 0; i < 2; ++i) {
            Map<LocalDate, WordleResultModel> models = this.testDataManager.getWordleResultsForUserId(Long.valueOf(i + 1));

            for(int j = 0; j < tests.size(); ++j) {
                LocalDate d = LocalDate.now().plusDays(j);
                WordleResultModel model = models.get(d);
                int testIdx = i == 0 ? j : tests.size() - 1 - j;
                assertEquals(tests.get(testIdx), model.getDbRepresentation());
            }
        }
    }

    @Test
    public void testWordleResultsForDate() {
        this.testDataManager.setUserName(1, "Alice");
        this.testDataManager.setUserName(2, "Bob");
        List<String> tests = List.of("11111 22222 33333", "11311 22311 33322 33333");

        for (int i = 0; i < tests.size(); ++i) {
            String aRes = tests.get(i);
            String bRes = tests.get(tests.size() - 1 - i);

            WordleResultModel aModel = WordleResultModel.from(aRes, i % 2 == 0);
            WordleResultModel bModel = WordleResultModel.from(bRes, i % 2 == 1);

            LocalDate d = LocalDate.now().plusDays(i);
            this.testDataManager.addWordleResult(1, d, aModel, i * 2);
            this.testDataManager.addWordleResult(2, d, bModel, i * 2 + 1);
        }

        for (int i = 0; i < tests.size(); ++i) {
            LocalDate d = LocalDate.now().plusDays(i);
            Map<Long, WordleResultModel> models = this.testDataManager.getWordleResultsForDate(d);

            WordleResultModel aModel = models.get(Long.valueOf(1));
            WordleResultModel bModel = models.get(Long.valueOf(2));

            assertEquals(tests.get(i), aModel.getDbRepresentation());
            assertEquals(tests.get(tests.size() - 1 - i), bModel.getDbRepresentation());
            assertEquals(i % 2 == 0, aModel.isHard());
            assertEquals(i % 2 == 1, bModel.isHard());
        }
    }

    @Test
    public void testMiniTimesFromDateRange() {
        this.testDataManager.setUserName(1, "Alice");
        this.testDataManager.setUserName(2, "Bob");

        LocalDate today = IChannelDataManager.MiniDate();
        this.testDataManager.addUserTime(1, 7, 1);
        this.testDataManager.addUserTime(2, 8, 2);
        LocalDate tomorrow = today.plusDays(1);
        this.testDataManager.addUserTime(1, tomorrow, 9, 3);
        this.testDataManager.addUserTime(2, tomorrow, 10, 4);

        var times = this.testDataManager.getTimesForDateInterval(today, tomorrow);
        assertEquals(2, times.size());
        assert(times.containsKey(Long.valueOf(1)));
        assert(times.containsKey(Long.valueOf(2)));

        var times1 = times.get(Long.valueOf(1));
        assertEquals(7, times1.get(today).getTime());
        assertEquals(1, times1.get(today).getMessageId().longValue());
        assertEquals(9, times1.get(tomorrow).getTime());
        assertEquals(3, times1.get(tomorrow).getMessageId().longValue());

        var times2 = times.get(Long.valueOf(2));
        assertEquals(8, times2.get(today).getTime());
        assertEquals(2, times2.get(today).getMessageId().longValue());
        assertEquals(10, times2.get(tomorrow).getTime());
        assertEquals(4, times2.get(tomorrow).getMessageId().longValue());
    }
}
