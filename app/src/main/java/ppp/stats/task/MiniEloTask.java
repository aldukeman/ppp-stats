package ppp.stats.task;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.model.UserModel;
import ppp.stats.logging.ILogger;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.MiniEloMessage;
import ppp.stats.task.utility.EloCalculator;
import ppp.stats.utility.Pair;

public class MiniEloTask implements ITask {
    private final ILogger logger;

    public MiniEloTask(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public List<IBotMessage> execute(IChannelDataManager dataManager) {
        this.logger.info("Starting SendMiniEloAction");

        // Get all times for all users
        final var users = dataManager.getUserModels();
        final List<Pair<UserModel, Map<LocalDate, Integer>>> userScorePairsList = users
            .entrySet()
            .stream()
            .map(user ->
                Pair.of(
                    user.getValue(),
                    dataManager.getTimesForUserId(user.getKey())
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                            Entry::getKey,
                            entry -> entry.getValue().getTime()))))
            .collect(Collectors.toList());
        
        // Get users who have submitted a time in the last week
        final LocalDate minDate = LocalDate.now().plusDays(-7);
        final var includedUserIds = userScorePairsList
            .stream()
            .filter(pair -> pair.second.keySet().stream().max((a, b) -> a.compareTo(b)).get().isAfter(minDate))
            .map(pair -> pair.first.getId())
            .collect(Collectors.toList());
        this.logger.info("Found " + includedUserIds.size() + " users with scores since min date");

        // Flip the map so it works for the EloCalculator
        final Map<LocalDate, Map<UserModel, Integer>> dateScoresMap = userScorePairsList.stream()
            .reduce(
                new HashMap<LocalDate, Map<UserModel, Integer>>(),
                (map, pair) -> {
                    Map<LocalDate, Map<UserModel, Integer>> tempMap = new HashMap<LocalDate, Map<UserModel, Integer>>();
                    tempMap.putAll(map);
                    pair.second.entrySet()
                        .forEach(entry -> {
                            map.computeIfAbsent(entry.getKey(), k -> new HashMap<UserModel, Integer>());
                            map.get(entry.getKey()).put(pair.first, entry.getValue());
                        });
                    return map;
                },
                (left, right) -> {
                    right.entrySet().stream()
                        .forEach(entry -> {
                            left.putAll(right);
                        });
                    return right;
                }
            );
        this.logger.info("Found " + dateScoresMap.size() + " dates");
        
        // Calculate Elo and remove users who haven't submitted a time in the last week
        final var eloScores = new EloCalculator()
            .calculateElo(dateScoresMap)
            .entrySet()
            .stream()
            .filter(entry -> includedUserIds.contains(entry.getKey().getId()))
            .map(entry -> Pair.of(entry.getKey().getName(), entry.getValue().intValue()))
            .sorted((a, b) -> b.second.intValue() - a.second.intValue())
            .collect(Collectors.toList());

        return List.of(new MiniEloMessage(eloScores));
    }

    @Override
    public LocalDateTime nextExecutionDateTime() {
        ZoneId nyt = ZoneId.of("America/New_York");
        LocalDate nextRunDay = LocalDate
            .now(nyt)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        LocalTime resetTime = LocalTime.of(20, 25, 2);
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
