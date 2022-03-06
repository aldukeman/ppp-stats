package ppp.stats.task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.model.UserModel;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.MiniResultsForDateMessage;
import ppp.stats.utility.Pair;

public class MiniResultsForDateTask implements ITask {
    @Override
    public IBotMessage execute(IChannelDataManager dataManager) {
        LocalDate date = IChannelDataManager.MiniDate();

        Map<Long, Integer> times = dataManager.getTimesForDate(date);
        Map<Long, UserModel> names = dataManager.getUserModels();
        List<Pair<String, Integer>> rows = times.entrySet().stream()
            .sorted((e_1, e_2) -> e_1.getValue().intValue() - e_2.getValue().intValue())
            .map(e -> new Pair<Long, Integer>(e.getKey(), e.getValue()))
            .map(e -> new Pair<String, Integer>(names.get(e.first).getName(), e.second))
            .toList();
        
        return new MiniResultsForDateMessage(date, rows);
    }

    @Override
    public LocalDateTime nextExecutionDateTime() {
        ZonedDateTime todaysReset = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 55)).atZone(ZoneId.of("America/New_York"));
        ZonedDateTime now = ZonedDateTime.now();
        Duration duration;
        if(todaysReset.isAfter(now)) {
            duration = Duration.between(now, todaysReset);
        } else {
            duration = Duration.between(now, todaysReset.plusDays(1));
        }
        return LocalDateTime.now().plus(duration);
    }
}
