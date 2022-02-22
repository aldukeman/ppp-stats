package ppp.stats.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import ppp.stats.data.model.UserModel;

public interface IDataManager {
    void setUserName(long id, String name);
    List<UserModel> getUserModels();
    void addUserTime(long id, int seconds);
    UserTimesDictionary getTimesForUserId(long id);
    Map<Long, Integer> getTimesForDate(LocalDate date);
}
