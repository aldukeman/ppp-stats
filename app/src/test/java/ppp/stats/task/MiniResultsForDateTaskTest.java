package ppp.stats.task;

import org.junit.Test;

import java.time.LocalDateTime;

public class MiniResultsForDateTaskTest {
    @Test public void testNextExecutionDate() {
        ITask task = new MiniResultsForDateTask();

        LocalDateTime next = task.nextExecutionDateTime();
        assert(LocalDateTime.now().isBefore(next));
    }
}