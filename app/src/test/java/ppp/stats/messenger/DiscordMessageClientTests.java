package ppp.stats.messenger;

import org.junit.Test;

import ppp.stats.utility.Pair;

import static org.junit.Assert.*;

import java.util.List;

public class DiscordMessageClientTests {
    @Test public void testMakeWinnersString() {
        DiscordMessageClient client = new DiscordMessageClient(null, null);

        List<Pair<String, Integer>> rows = List.of(Pair.of("Rush", 5));
        assertEquals(client.makeWinnersString(rows), "Rush");

        rows = List.of(Pair.of("Rush", 5), Pair.of("Ian", 5));
        assertEquals(client.makeWinnersString(rows), "Rush and Ian");

        rows = List.of(Pair.of("Rush", 5), Pair.of("Ian", 5), Pair.of("Anton", 5));
        assertEquals(client.makeWinnersString(rows), "Rush, Ian, and Anton");
    }
}
