package ppp.stats.data;

import java.util.Set;

public interface IDataManager {
    void setUserName(long id, String name);
    void addUserTime(long id, int seconds);
    Set<Long> getUserIds();
    UserTimesDictionary getTimesForUserId(long id);
}
