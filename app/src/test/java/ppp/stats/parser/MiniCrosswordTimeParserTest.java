package ppp.stats.parser;

import org.junit.Test;

import ppp.stats.action.ProcessMiniTimeAction;
import ppp.stats.models.MessageMock;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

public class MiniCrosswordTimeParserTest {
    @Test public void testMiniCrosswordTimeParser() {
        MiniCrosswordTimeParser parser = new MiniCrosswordTimeParser();

        Hashtable<String, Integer> stringPositives = new Hashtable<>();
        stringPositives.put("0:34 mini", 34);
        stringPositives.put("2:30 mini", 150);
        stringPositives.put("12:07 mini", 727);

        MessageMock mock = new MessageMock();
        for(Map.Entry<String, Integer> e: stringPositives.entrySet()) {
            mock.content = e.getKey();
            assertTrue(parser.parse(mock) instanceof ProcessMiniTimeAction);
        }

        HashSet<String> stringNegatives = new HashSet<>();
        stringNegatives.add("4 mini");
        stringNegatives.add("0:68 mini");
        stringNegatives.add("my mini time was 4:20");
        for(String s: stringNegatives) {
            mock.content = s;
            assertNull(parser.parse(mock));
        }
    }
}
