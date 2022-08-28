package ppp.stats.task.utility;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.InMemoryDataManager;
import ppp.stats.data.model.UserModel;

public class EloCalculatorTest {
    @Test
    public void testSingleEntry() {
        var calc = new EloCalculator(1000, 400, 32);

        IChannelDataManager dataManager = new InMemoryDataManager();
        dataManager.setUserName(1, "Alice");
        dataManager.setUserName(2, "Bob");
        dataManager.setUserName(3, "Charlie");
        dataManager.setUserName(4, "Dixon");

        var start = LocalDate.now();
        var end = start;
        dataManager.addUserTime(1, end, 20, 0);
        dataManager.addUserTime(2, end, 30, 1);
        dataManager.addUserTime(3, end, 40, 2);

        var users = dataManager.getUserModels();
        var times = dataManager.getTimesForDateInterval(start, end);
        
        Map<LocalDate, Map<UserModel, Integer>> scores = times.entrySet().stream()
            .map(e -> e.getValue().keySet().stream().toList())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet()).stream()
            .collect(Collectors.toMap(Function.identity(), date -> 
                users.entrySet().stream()
                    .filter(user -> times.containsKey(user.getKey()))
                    .filter(user -> times.get(user.getKey()).keySet().contains(date) )
                    .collect(Collectors.toMap(Entry::getValue, user -> times.get(user.getKey()).get(date).getTime()))
            ));
        var elos = calc.calculateElo(scores);

        assertEquals(1021.333, elos.get(users.get(Long.valueOf(1))).doubleValue(), 0.001);
        assertEquals(1000, elos.get(users.get(Long.valueOf(2))).doubleValue(), 0.001);
        assertEquals(978.666, elos.get(users.get(Long.valueOf(3))).doubleValue(), 0.001);
    }

    @Test
    public void testTie() {
        var calc = new EloCalculator(1000, 400, 32);

        IChannelDataManager dataManager = new InMemoryDataManager();
        dataManager.setUserName(1, "Alice");
        dataManager.setUserName(2, "Bob");
        dataManager.setUserName(3, "Charlie");
        dataManager.setUserName(4, "Dixon");

        var start = LocalDate.now();
        var end = start;
        dataManager.addUserTime(1, end, 30, 0);
        dataManager.addUserTime(2, end, 30, 1);
        dataManager.addUserTime(3, end, 30, 2);

        var users = dataManager.getUserModels();
        var times = dataManager.getTimesForDateInterval(start, end);
        
        Map<LocalDate, Map<UserModel, Integer>> scores = times.entrySet().stream()
            .map(e -> e.getValue().keySet().stream().toList())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet()).stream()
            .collect(Collectors.toMap(Function.identity(), date -> 
                users.entrySet().stream()
                    .filter(user -> times.containsKey(user.getKey()))
                    .filter(user -> times.get(user.getKey()).keySet().contains(date) )
                    .collect(Collectors.toMap(Entry::getValue, user -> times.get(user.getKey()).get(date).getTime()))
            ));
        var elos = calc.calculateElo(scores);

        assertEquals(1000, elos.get(users.get(Long.valueOf(1))).doubleValue(), 0.001);
        assertEquals(1000, elos.get(users.get(Long.valueOf(2))).doubleValue(), 0.001);
        assertEquals(1000, elos.get(users.get(Long.valueOf(3))).doubleValue(), 0.001);
    }
}