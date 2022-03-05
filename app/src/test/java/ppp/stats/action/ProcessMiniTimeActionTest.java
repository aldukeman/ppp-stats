package ppp.stats.action;

import org.junit.Test;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.InMemoryDataManager;
import ppp.stats.data.model.UserModel;
import ppp.stats.logging.NoOpLogger;
import ppp.stats.messenger.message.AcknowledgeMessage;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.models.MessageMock;
import ppp.stats.models.UserMock;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Map;

public class ProcessMiniTimeActionTest {
    @Test public void testFirstAddition() {
        UserMock user = new UserMock();
        user.id = 1;
        user.username = "Alice";

        MessageMock msg = new MessageMock();

        IChannelDataManager dm = new InMemoryDataManager();
        ProcessMiniTimeAction action = new ProcessMiniTimeAction(user, 7, new NoOpLogger());
        IBotMessage botMsg = action.process(msg, dm);
        assert(botMsg instanceof AcknowledgeMessage);

        Map<LocalDate, Integer> dict = dm.getTimesForUserId(user.getId());
        assertEquals(dict.size(), 1);
        assertEquals(dict.get(IChannelDataManager.MiniDate()), Integer.valueOf(7));

        Map<Long, UserModel> users = dm.getUserModels();
        assertEquals(users.size(), 1);
        UserModel userModel = users.get(Long.valueOf(1));
        assertEquals(userModel.getId(), 1);
        assertEquals(userModel.getName(), "Alice");

        user.id = 2;
        user.username = "Bob";
        action = new ProcessMiniTimeAction(user, 8, new NoOpLogger());
        botMsg = action.process(msg, dm);
        assert(botMsg instanceof AcknowledgeMessage);

        dict = dm.getTimesForUserId(user.getId());
        assertEquals(dict.size(), 1);
        assertEquals(dict.get(IChannelDataManager.MiniDate()), Integer.valueOf(8));

        users = dm.getUserModels();
        assertEquals(users.size(), 2);
        userModel = users.get(Long.valueOf(1));
        assertEquals(userModel.getId(), 1);
        assertEquals(userModel.getName(), "Alice");
        userModel = users.get(Long.valueOf(2));
        assertEquals(userModel.getId(), 2);
        assertEquals(userModel.getName(), "Bob");
    }

    @Test public void testAdditionalTimes() {
        UserMock user = new UserMock();
        user.id = 1;
        user.username = "Alice";

        MessageMock msg = new MessageMock();

        IChannelDataManager dm = new InMemoryDataManager();
        dm.setUserName(1, "Alice");
        LocalDate yesterday = IChannelDataManager.MiniDate().plusDays(-1);
        dm.addUserTime(1, yesterday, 7);
        ProcessMiniTimeAction action = new ProcessMiniTimeAction(user, 8, new NoOpLogger());
        IBotMessage botMsg = action.process(msg, dm);
        assert(botMsg instanceof AcknowledgeMessage);

        Map<LocalDate, Integer> dict = dm.getTimesForUserId(user.getId());
        assertEquals(dict.size(), 2);
        assertEquals(dict.get(yesterday), Integer.valueOf(7));
        assertEquals(dict.get(IChannelDataManager.MiniDate()), Integer.valueOf(8));
    }
}
