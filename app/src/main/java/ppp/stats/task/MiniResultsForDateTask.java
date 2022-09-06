package ppp.stats.task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.model.MiniTimeMessageModel;
import ppp.stats.data.model.UserModel;
import ppp.stats.logging.ILogger;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.MiniResultsForDateMessage;
import ppp.stats.messenger.message.ReactToMessage;
import ppp.stats.utility.Pair;

public class MiniResultsForDateTask implements ITask {
    private final ILogger logger;

    public MiniResultsForDateTask(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public List<IBotMessage> execute(IChannelDataManager dataManager) {
        List<IBotMessage> messages = new ArrayList<>();
        LocalDate date = IChannelDataManager.MiniDate();
        this.logger.trace("Executing " + this + " for date: " + date);

        var times = dataManager.getTimesForDate(date);
        this.logger.trace("Found " + times.size() + " results to send");
        if(times.size() == 0) { return new ArrayList<>(); }

        List<Pair<Long, MiniTimeMessageModel>> sortedTimes = times.entrySet().stream()
            .sorted((e_1, e_2) -> e_1.getValue().getTime() - e_2.getValue().getTime())
            .map(e -> Pair.of(e.getKey(), e.getValue()))
            .toList();

        Map<Long, UserModel> names = dataManager.getUserModels();
        List<Pair<String, Integer>> messageRows = sortedTimes.stream()
            .map(e -> Pair.of(names.get(e.first).getName(), Integer.valueOf(e.second.getTime())))
            .toList();
        messages.add(new MiniResultsForDateMessage(date, messageRows));
        this.logger.trace("Creeated results message to send");

        List<Long> winningMessages = new ArrayList<>();
        int min = sortedTimes.get(0).second.getTime();
        winningMessages.add(sortedTimes.get(0).second.getMessageId());
        for(int i = 1; i < sortedTimes.size() && min == sortedTimes.get(i).second.getTime(); ++i) {
            winningMessages.add(sortedTimes.get(i).second.getMessageId());
        }
        this.logger.trace("Found " + winningMessages.size() + " winning time(s) of " + min + " seconds");
        for(Long msgId: winningMessages) {
            messages.add(new ReactToMessage(msgId, "🧠"));
        }

        int max = sortedTimes.get(sortedTimes.size() - 1).second.getTime();
        if(max > min) {
            List<Long> losingMessages = new ArrayList<>();
            for(int i = sortedTimes.size() - 1; i > 0 && max == sortedTimes.get(i).second.getTime(); --i) {
                losingMessages.add(sortedTimes.get(i).second.getMessageId());
            }
            this.logger.trace("Found " + losingMessages.size() + " losing time(s) of " + max + " seconds");
            for(Long msgId: losingMessages) {
                messages.add(new ReactToMessage(msgId, "🥦"));
            }
        }

        this.logger.trace("Returning " + messages.size() + " messages to send to chat");
        return messages;
    }

    @Override
    public LocalDateTime nextExecutionDateTime() {
        ZoneId nyt = ZoneId.of("America/New_York");
        ZonedDateTime todaysReset = LocalDateTime.of(LocalDate.now(nyt), LocalTime.of(20, 25)).atZone(nyt);
        ZonedDateTime now = ZonedDateTime.now(nyt);
        Duration duration;
        if(todaysReset.isAfter(now)) {
            duration = Duration.between(now, todaysReset);
        } else {
            duration = Duration.between(now, todaysReset.plusDays(1));
        }
        return LocalDateTime.now().plus(duration);
    }
}
