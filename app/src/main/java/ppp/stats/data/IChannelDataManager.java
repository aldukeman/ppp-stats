package ppp.stats.data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import ppp.stats.data.model.UserModel;

public interface IChannelDataManager {
    void setUserName(long id, String name);
    Map<Long, UserModel> getUserModels();
    void addUserTime(long id, LocalDate date, int seconds);
    Map<LocalDate, Integer> getTimesForUserId(long id);
    Map<Long, Integer> getTimesForDate(LocalDate date);

    static LocalDate MiniDate() {
        return LocalDate.now(ZoneId.of("America/New_York"));
    }

    default void addUserTime(long id, int seconds) {
        this.addUserTime(id, IChannelDataManager.MiniDate(), seconds);
    }
}
