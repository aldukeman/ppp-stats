package ppp.stats.task.utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ppp.stats.data.model.UserModel;

// Base on https://towardsdatascience.com/developing-a-generalized-elo-rating-system-for-multiplayer-games-b9b495e87802
// The basic idea is each round (daily mini results) has an expectation for results based on each players Elo. The scores
// for each round are based on the actual daily rankings of the mini times. Magnitude of victory is not considered. The 
// difference between a player's score and their expectation determines the change in their Elo for the day.
public class EloCalculator {
    private final double INIT_SCORE;
    private final int D;
    private final int K;

    public EloCalculator() {
        this(1000, 400, 32);
    }

    public EloCalculator(int initScore, int d, int k) {
        this.INIT_SCORE = initScore;
        this.D = d;
        this.K = k;
    }

    public Map<UserModel, Double> calculateElo(final Map<LocalDate, Map<UserModel, Integer>> data) {
        Map<UserModel, Double> eloMap = new HashMap<>();

        data.entrySet().stream()
            .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
            .map(entry -> entry.getValue())
            .filter(entry -> entry.size() >= 2)
            .forEachOrdered(entry -> {
                List<UserModel> users = entry.keySet().stream().toList();
                int userCount = users.size();
                if(userCount == 0) { return; }
                users.forEach(user -> { eloMap.computeIfAbsent(user, u -> this.INIT_SCORE); });

                List<Double> scores = this.scoresForNumPlayers(entry.size());
                Map<UserModel, Double> preElos = eloMap.entrySet().stream()
                    .filter(e -> { return users.contains(e.getKey()); })
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
                Map<UserModel, Double> expectations = this.expectations(preElos);

                entry.entrySet().stream()
                    .collect(Collectors.toMap(
                        Entry::getValue,
                        e -> List.of(e.getKey()),
                        (a, b) -> Stream.of(a, b).flatMap(Collection::stream).collect(Collectors.toList())))
                    .entrySet().stream()
                    .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
                    .forEachOrdered(usersWithScore -> {
                        var tiedUsers = usersWithScore.getValue();
                        var usersScores = scores.subList(0, tiedUsers.size());
                        double totalScore = usersScores.stream().mapToDouble(Double::doubleValue).sum();
                        double avgScore = totalScore / tiedUsers.size();
                        scores.subList(0, tiedUsers.size()).clear();

                        tiedUsers.forEach(user -> {
                            double elo = eloMap.get(user);
                            double diff = this.K * (userCount - 1) * (avgScore - expectations.get(user));
                            eloMap.put(user, elo + diff);
                        });
                    });
            });

        return eloMap;
    }

    private Map<UserModel, Double> expectations(Map<UserModel, Double> elos) {
        return elos.entrySet()
            .stream()
            .collect(Collectors.toMap(Entry::getKey, e -> {
                return Double.valueOf(this.expectation(e.getKey(), elos));
            }));
    }

    private double expectation(UserModel user, Map<UserModel, Double> elos) {
        double playerElo = elos.get(user).doubleValue();
        double numerator = elos.entrySet().stream()
            .map(entry -> {
                if(entry.getKey().getId() == user.getId()) { return 0.0; }
                double opponentElo = entry.getValue().doubleValue();
                return 1.0 / (1 + Math.pow(10, (opponentElo - playerElo) / this.D));
            })
            .mapToDouble(Double::doubleValue)
            .sum();
        
        int playerCount = elos.size();
        int denominator = playerCount * (playerCount - 1) / 2;

        return numerator / denominator;
    }

    private List<Double> scoresForNumPlayers(int num) {
        List<Double> scores = new ArrayList<>(num);
        for(int i = 0; i < num; ++i) {
            scores.add(this.linearScoreFunction(i + 1, num));
        }
        return scores;
    }

    private double linearScoreFunction(int place, int numPlayers) {
        int sum = numPlayers * (numPlayers - 1) / 2;
        return ((double)numPlayers - place) / sum;
    }
}
