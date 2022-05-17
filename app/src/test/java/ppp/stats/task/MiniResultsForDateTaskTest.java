package ppp.stats.task;

import org.junit.Test;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.InMemoryDataManager;
import ppp.stats.logging.NoOpLogger;
import ppp.stats.messenger.message.MiniResultsForDateMessage;
import ppp.stats.messenger.message.ReactToMessage;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MiniResultsForDateTaskTest {
    @Test
    public void testNextExecutionDate() {
        ITask task = new MiniResultsForDateTask(new NoOpLogger());
        LocalDateTime next = task.nextExecutionDateTime();
        assert(LocalDateTime.now().isBefore(next));
        assert(Duration.between(LocalDateTime.now(), next).toSeconds() < TimeUnit.DAYS.toSeconds(1));
    }

    @Test
    public void testExecution() {
        ITask task = new MiniResultsForDateTask(new NoOpLogger());
        
        IChannelDataManager dataManager = new InMemoryDataManager();
        dataManager.setUserName(1, "Alice");
        dataManager.setUserName(2, "Bob");
        dataManager.addUserTime(1, 20, 21);
        dataManager.addUserTime(2, 40, 41);

        var messages = task.execute(dataManager);
        assert(messages.size() == 3);

        long numResultsMessages = messages
            .stream()
            .filter(msg -> { return msg instanceof MiniResultsForDateMessage; })
            .count();
        assertEquals(1, numResultsMessages);

        List<ReactToMessage> brain = messages
            .stream()
            .filter(msg -> { return msg instanceof ReactToMessage; })
            .map(msg -> { return (ReactToMessage)msg; })
            .filter(msg -> { return msg.reaction == "🧠"; })
            .toList();
        assertEquals(1, brain.size());
        assertEquals(21, brain.get(0).msgId);

        List<ReactToMessage> broccoli = messages
            .stream()
            .filter(msg -> { return msg instanceof ReactToMessage; })
            .map(msg -> { return (ReactToMessage)msg; })
            .filter(msg -> { return msg.reaction == "🥦"; })
            .toList();
        assertEquals(1, broccoli.size());
        assertEquals(41, broccoli.get(0).msgId);

        dataManager = new InMemoryDataManager();
        dataManager.setUserName(1, "Alice");
        dataManager.addUserTime(1, 20, 22);

        messages = task.execute(dataManager);
        assert(messages.size() == 2);

        numResultsMessages = messages
            .stream()
            .filter(msg -> { return msg instanceof MiniResultsForDateMessage; })
            .count();
        assertEquals(1, numResultsMessages);

        brain = messages
            .stream()
            .filter(msg -> { return msg instanceof ReactToMessage; })
            .map(msg -> { return (ReactToMessage)msg; })
            .filter(msg -> { return msg.reaction == "🧠"; })
            .toList();
        assertEquals(1, brain.size());
        assertEquals(22, brain.get(0).msgId);

        broccoli = messages
            .stream()
            .filter(msg -> { return msg instanceof ReactToMessage; })
            .map(msg -> { return (ReactToMessage)msg; })
            .filter(msg -> { return msg.reaction == "🥦"; })
            .toList();
        assert(broccoli.isEmpty());
    }
}