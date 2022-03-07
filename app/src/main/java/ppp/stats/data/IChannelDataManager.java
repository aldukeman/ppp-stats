package ppp.stats.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import ppp.stats.data.model.UserModel;

public interface IChannelDataManager {
    void setUserName(long id, String name);
    Map<Long, UserModel> getUserModels();
    void addUserTime(long id, LocalDate date, int seconds);
    Map<LocalDate, Integer> getTimesForUserId(long id);
    Map<Long, Integer> getTimesForDate(LocalDate date);

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

    default void addUserTime(long id, int seconds) {
        this.addUserTime(id, IChannelDataManager.MiniDate(), seconds);
    }
}
