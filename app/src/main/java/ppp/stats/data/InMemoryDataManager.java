package ppp.stats.data;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ppp.stats.data.model.UserModel;

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
    public List<UserModel> getUserModels() {
        return this.userNames.entrySet().stream().map(e -> new UserModel(e.getKey(), e.getValue())).toList();
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
    public UserTimesDictionary getTimesForUserId(long id) {
        return this.userTimes.get(Long.valueOf(id));
    }

    @Override
    public Map<Long, Integer> getTimesForDate(LocalDate date) {
        Hashtable<Long, Integer> results = new Hashtable<>();
        var iter = this.userTimes.entrySet().iterator();
        while(iter.hasNext()) {
            var entry = iter.next();
            Long id = entry.getKey();
            Integer time = entry.getValue().get(date);
            results.put(id, time);
        }
        return results;
    }
}
