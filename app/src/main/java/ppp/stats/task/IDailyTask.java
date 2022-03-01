package ppp.stats.task;

import java.time.LocalTime;

public interface IDailyTask {
    LocalTime executionTime();
    void execute();
}
