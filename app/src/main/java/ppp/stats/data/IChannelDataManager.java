package ppp.stats.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.Map;

import ppp.stats.data.model.MiniTimeMessageModel;
import ppp.stats.data.model.UserModel;
import ppp.stats.data.model.WordleResultModel;

public interface IChannelDataManager {
    void setUserName(long userId, String name);
    Map<Long, UserModel> getUserModels();
    void addUserTime(long userId, LocalDate date, int seconds, long messageId);
    Map<LocalDate, MiniTimeMessageModel> getTimesForUserId(long userId);
    Map<Long, MiniTimeMessageModel> getTimesForDate(LocalDate date);
    void addWordleResult(long userId, LocalDate date, WordleResultModel model, long messageId);
    Map<LocalDate, WordleResultModel> getWordleResultsForUserId(long userId);
    Map<Long, WordleResultModel> getWordleResultsForDate(LocalDate date);

    static LocalDate MiniDate() {
        ZoneId nyt = ZoneId.of("America/New_York");
        ZonedDateTime todaysReset = LocalDateTime.of(LocalDate.now(nyt), LocalTime.of(20, 30)).atZone(nyt);
        ZonedDateTime now = ZonedDateTime.now(nyt);
        if(now.isBefore(todaysReset)) {
            return todaysReset.toLocalDate();
        } else {
            return todaysReset.toLocalDate().plusDays(1);
        }
    }

    static LocalDate WordleDate() {
        ZoneId nyt = ZoneId.of("America/New_York");
        ZonedDateTime todaysReset = LocalDateTime.of(LocalDate.now(nyt), LocalTime.of(3, 0)).atZone(nyt);
        ZonedDateTime now = ZonedDateTime.now(nyt);
        if(now.isBefore(todaysReset)) {
            return todaysReset.toLocalDate();
        } else {
            return todaysReset.toLocalDate().plusDays(1);
        }
    }

    default void addUserTime(long userId, int seconds, long messageId) {
        this.addUserTime(userId, IChannelDataManager.MiniDate(), seconds, messageId);
    }

    default void addWordleResult(long userId, WordleResultModel model, long messageId) {
        this.addWordleResult(userId, IChannelDataManager.WordleDate(), model, messageId);
    }

    default Map<Long, Map<LocalDate, MiniTimeMessageModel>> getTimesForDateInterval(LocalDate start, LocalDate end) {
        if(end.isBefore(start)) {
            return new Hashtable<>();
        }

        Map<Long, Map<LocalDate, MiniTimeMessageModel>> times = new Hashtable<>();
        start.datesUntil(end.plusDays(1)).forEach((date) -> {
            this.getTimesForDate(date).forEach((key, value) -> {
                var userTimes = times.get(key);
                if(userTimes == null) {
                    userTimes = new Hashtable<>();
                    times.put(key, userTimes);
                }
                userTimes.put(date, value);
            });
        });

        return times;
    }
}
