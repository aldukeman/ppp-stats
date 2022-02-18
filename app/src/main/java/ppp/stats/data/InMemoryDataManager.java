package ppp.stats.data;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Set;

public class InMemoryDataManager implements IDataManager {
    private Hashtable<BigInteger, UserTimesDictionary> userTimes;
    private Hashtable<BigInteger, String> userNames;

    public InMemoryDataManager() {
        this.userTimes = new Hashtable<>();
        this.userNames = new Hashtable<>();
    }

    @Override
    public void setUserName(BigInteger id, String name) {
        this.userNames.put(id, name);
    }

    @Override
    public void addUserTime(BigInteger id, int seconds) {
        UserTimesDictionary dict = this.userTimes.get(id);
        if(dict != null) {
            LocalDate now = LocalDate.now();
            dict.put(now, seconds);
        }
    }

    @Override
    public Set<BigInteger> getUserIds() {
        return this.userNames.keySet();
    }

    @Override
    public UserTimesDictionary getTimesForUserId(BigInteger id) {
        return this.userTimes.get(id);
    }
}
