package ppp.stats.task;

import java.time.LocalTime;
import java.time.ZoneOffset;

import ppp.stats.data.IDataManager;
import ppp.stats.logging.ILogger;

public class DailyResultsTask implements IDailyTask {
    private ILogger logger;

    public DailyResultsTask(ILogger logger, IDataManager dataManager) {
        this.logger = logger;
    }

    @Override
    public LocalTime executionTime() {
        return LocalTime.of(23, 45).atOffset(ZoneOffset.of("America/New_York")).toLocalTime();
    }

    @Override
    public void execute() {
        this.logger.trace("executing");
    }
}
