package ppp.stats.data;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Map;

import ppp.stats.data.model.MiniTimeMessageModel;
import ppp.stats.data.model.UserModel;

public class InMemoryDataManager implements IChannelDataManager {
    private Hashtable<Long, Hashtable<LocalDate, MiniTimeMessageModel>> userTimes;
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
    public void addUserTime(long userId, LocalDate date, int seconds, long messageId) {
        Hashtable<LocalDate, MiniTimeMessageModel> dict = this.userTimes.get(Long.valueOf(userId));
        MiniTimeMessageModel model = new MiniTimeMessageModel(messageId, seconds, userId);
        if(dict != null) {
            dict.put(date, model);
        } else {
            dict = new Hashtable<LocalDate, MiniTimeMessageModel>();
            dict.put(date, model);
            this.userTimes.put(Long.valueOf(userId), dict);
        }
    }

    @Override
    public Map<LocalDate, MiniTimeMessageModel> getTimesForUserId(long id) {
        return this.userTimes.get(Long.valueOf(id));
    }

    @Override
    public Map<Long, MiniTimeMessageModel> getTimesForDate(LocalDate date) {
        Hashtable<Long, MiniTimeMessageModel> results = new Hashtable<>();
        for(var entry: this.userTimes.entrySet()) {
            Long id = entry.getKey();
            MiniTimeMessageModel model = entry.getValue().get(date);
            if(model != null) {
                results.put(id, model);
            }
        }
        return results;
    }
}
