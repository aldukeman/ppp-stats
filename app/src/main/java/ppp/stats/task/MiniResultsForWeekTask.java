package ppp.stats.task;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.MiniResultsForDateIntervalMessage;
import ppp.stats.utility.Pair;

public class MiniResultsForWeekTask implements ITask {
    private final ILogger logger;

    public MiniResultsForWeekTask(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public List<IBotMessage> execute(IChannelDataManager dataManager) {
        var end = IChannelDataManager.MiniDate();
        var start = end.plusDays(-6);
        this.logger.trace("Executing " + this + " for start: " + start + ", end: " + end);

        var users = dataManager.getUserModels();
        var timesList = dataManager
                .getTimesForDateInterval(start, end)
                .entrySet()
                .stream()
                .filter((entry) -> entry.getValue().size() == 7)
                .map((entry) -> {
                    int sum = entry
                            .getValue()
                            .entrySet()
                            .stream()
                            .map((e) -> e.getValue().getTime())
                            .reduce(0, (a, b) -> a + b);
                    var userModel = users.get(entry.getKey());
                    if (userModel == null) {
                        return null;
                    }
                    return Pair.of(userModel.getName(), Float.valueOf((float) (sum / 7.0)));
                })
                .filter((pair) -> pair != null)
                .sorted((a, b) -> (int) ((a.second.floatValue() - b.second.floatValue()) * 10))
                .toList();

        return List.of(new MiniResultsForDateIntervalMessage(start, end, timesList));
    }

    @Override
    public LocalDateTime nextExecutionDateTime() {
        ZoneId nyt = ZoneId.of("America/New_York");
        LocalDate nextRunDay = LocalDate
            .now(nyt)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalTime resetTime = LocalTime.of(20, 25, 1);
        ZonedDateTime nextRun = LocalDateTime.of(nextRunDay, resetTime).atZone(nyt);
        ZonedDateTime now = ZonedDateTime.now(nyt);

        Duration duration;
        if(nextRun.isAfter(now)) {
            duration = Duration.between(now, nextRun);
        } else {
            duration = Duration.between(now, nextRun.plusDays(7));
        }
        return LocalDateTime.now().plus(duration);
    }
}
