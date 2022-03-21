package ppp.stats.data;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Map;

import ppp.stats.data.model.MiniTimeMessageModel;
import ppp.stats.data.model.UserModel;
import ppp.stats.data.model.WordleResultModel;

public class InMemoryDataManager implements IChannelDataManager {
    private Hashtable<Long /* userId */, Hashtable<LocalDate, MiniTimeMessageModel>> userTimes;
    private Hashtable<Long /* userId */, String> userNames;
    private Hashtable<Long /* userId */, Hashtable<LocalDate, WordleResultModel>> wordleResults;

    public InMemoryDataManager() {
        this.userTimes = new Hashtable<>();
        this.userNames = new Hashtable<>();
        this.wordleResults = new Hashtable<>();
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
            dict = new Hashtable<>();
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

    @Override
    public void addWordleResult(long userId, LocalDate date, WordleResultModel model, long messageId) {
        var userResults = this.wordleResults.get(Long.valueOf(userId));
        if(userResults != null) {
            userResults.put(date, model);
        } else {
            userResults = new Hashtable<>();
            userResults.put(date, model);
            this.wordleResults.put(Long.valueOf(userId), userResults);
        }
    }

    @Override
    public Map<LocalDate, WordleResultModel> getWordleResultsForUserId(long userId) {
        return this.wordleResults.get(Long.valueOf(userId));
    }
}
