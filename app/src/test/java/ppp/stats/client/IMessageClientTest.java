package ppp.stats.client;

import java.util.Hashtable;

import org.junit.Test;

import junit.framework.TestCase;

public class IMessageClientTest extends TestCase {
    @Test
    public void testTimeString() {
        IMessageClient msgClient = new IMessageClientMock();

        Hashtable<Integer, String> intMap = new Hashtable<>();
        intMap.put(Integer.valueOf(97), "1:37");
        intMap.put(Integer.valueOf(67), "1:07");
        intMap.put(Integer.valueOf(727), "12:07");
        intMap.put(Integer.valueOf(37), "0:37");
        for(Integer i: intMap.keySet()) {
            assertEquals(msgClient.timeString(i), intMap.get(i));
        }

        Hashtable<Float, String> floatMap = new Hashtable<>();
        floatMap.put(Float.valueOf(97.1f), "1:37.1");
        floatMap.put(Float.valueOf(67.2f), "1:07.2");
        floatMap.put(Float.valueOf(727.3f), "12:07.3");
        floatMap.put(Float.valueOf(37.4f), "0:37.4");
        for(Float f: floatMap.keySet()) {
            assertEquals(msgClient.timeString(f), floatMap.get(f));
        }
    }
}
