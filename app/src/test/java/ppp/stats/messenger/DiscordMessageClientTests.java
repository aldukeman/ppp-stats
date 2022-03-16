package ppp.stats.messenger;

import org.junit.Test;

import ppp.stats.utility.Pair;

import static org.junit.Assert.*;

import java.util.List;

public class DiscordMessageClientTests {
    @Test public void testMakeWinnersString() {
        DiscordMessageClient client = new DiscordMessageClient(null, null);

        List<Pair<String, Integer>> rows = List.of(new Pair("Rush", 5));
        assertEquals(client.makeWinnersString(rows), "Rush");

        rows = List.of(new Pair("Rush", 5), new Pair("Ian", 5));
        assertEquals(client.makeWinnersString(rows), "Rush and Ian");

        rows = List.of(new Pair("Rush", 5), new Pair("Ian", 5), new Pair("Anton", 5));
        assertEquals(client.makeWinnersString(rows), "Rush, Ian, and Anton");

    }
}
