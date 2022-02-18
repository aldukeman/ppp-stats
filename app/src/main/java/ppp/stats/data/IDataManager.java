package ppp.stats.data;

import java.math.BigInteger;
import java.util.Set;

interface IDataManager {
    void setUserName(BigInteger id, String name);
    void addUserTime(BigInteger id, int seconds);
    Set<BigInteger> getUserIds();
    UserTimesDictionary getTimesForUserId(BigInteger id);
}
