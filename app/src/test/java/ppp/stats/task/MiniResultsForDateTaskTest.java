package ppp.stats.task;

import org.junit.Test;

import ppp.stats.logging.NoOpLogger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class MiniResultsForDateTaskTest {
    @Test public void testNextExecutionDate() {
        ITask task = new MiniResultsForDateTask(new NoOpLogger());
        LocalDateTime next = task.nextExecutionDateTime();
        assert(LocalDateTime.now().isBefore(next));
        assert(Duration.between(LocalDateTime.now(), next).toSeconds() < TimeUnit.DAYS.toSeconds(1));
    }
}