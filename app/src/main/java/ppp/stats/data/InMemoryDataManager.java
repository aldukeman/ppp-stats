package ppp.stats.data;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Set;

public class InMemoryDataManager implements IDataManager {
    private Hashtable<Long, UserTimesDictionary> userTimes;
    private Hashtable<Long, String> userNames;

    public InMemoryDataManager() {
        this.userTimes = new Hashtable<>();
        this.userNames = new Hashtable<>();
    }

    @Override
    public void setUserName(long id, String name) {
        this.userNames.put(Long.valueOf(id), name);
    }

    @Override
    public void addUserTime(long id, int seconds) {
        UserTimesDictionary dict = this.userTimes.get(Long.valueOf(id));
        LocalDate now = LocalDate.now();
        if(dict != null) {
            dict.put(now, seconds);
        } else {
            dict = new UserTimesDictionary();
            dict.put(now, seconds);
            this.userTimes.put(Long.valueOf(id), dict);
        }
    }

    @Override
    public Set<Long> getUserIds() {
        return this.userNames.keySet();
    }

    @Override
    public UserTimesDictionary getTimesForUserId(long id) {
        return this.userTimes.get(Long.valueOf(id));
    }
}
