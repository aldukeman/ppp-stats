package ppp.stats.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
        LocalTime time = LocalTime.now(ZoneId.of("America/New_York")).minusMinutes(15);
        if(LocalTime.now().isAfter(time)) {
            return LocalDateTime.of(LocalDate.now().plusDays(1), time);
        } else {
            return LocalDateTime.of(LocalDate.now(), time);
        }
    }
}
