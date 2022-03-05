package ppp.stats.data;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Map;

import ppp.stats.data.model.UserModel;

public class InMemoryDataManager implements IChannelDataManager {
    private Hashtable<Long, Hashtable<LocalDate, Integer>> userTimes;
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
    public Map<Long, UserModel> getUserModels() {
        Hashtable<Long, UserModel> map = new Hashtable<>();
        for(Map.Entry<Long, String> entry: this.userNames.entrySet()) {
            map.put(entry.getKey(), new UserModel(entry.getKey(), entry.getValue()));
        }
        return map;
    }

    @Override
    public void addUserTime(long id, LocalDate date, int seconds) {
        Hashtable<LocalDate, Integer> dict = this.userTimes.get(Long.valueOf(id));
        if(dict != null) {
            dict.put(date, seconds);
        } else {
            dict = new Hashtable<LocalDate, Integer>();
            dict.put(date, seconds);
            this.userTimes.put(Long.valueOf(id), dict);
        }
    }

    @Override
    public Map<LocalDate, Integer> getTimesForUserId(long id) {
        return this.userTimes.get(Long.valueOf(id));
    }

    @Override
    public Map<Long, Integer> getTimesForDate(LocalDate date) {
        Hashtable<Long, Integer> results = new Hashtable<>();
        for(var entry: this.userTimes.entrySet()) {
            Long id = entry.getKey();
            Integer time = entry.getValue().get(date);
            results.put(id, time);
        }
        return results;
    }
}
