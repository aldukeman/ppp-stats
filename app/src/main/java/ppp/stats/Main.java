package ppp.stats;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ppp.stats.bot.DiscordPPPBot;
import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.SQLiteDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.parser.CommandParser;
import ppp.stats.parser.IParser;
import ppp.stats.parser.MiniCrosswordTimeParser;
import ppp.stats.parser.WordleResultParser;
import ppp.stats.parser.command.StatsCommand;
import ppp.stats.parser.command.TimesCommand;
import ppp.stats.parser.command.TrollCommand;
import ppp.stats.task.ITask;
import ppp.stats.task.MiniEloTask;
import ppp.stats.task.MiniResultsForDateTask;
import ppp.stats.task.MiniResultsForWeekTask;

public class Main {
    public static void main(String[] args) {
        ILogger logger = new SystemOutLogger();

        final String TOKEN_ENV_VAR = "DISCORD_BOT_TOKEN";
        final String token;
        if (args.length >= 1) {
            token = args[0];
        } else if (System.getenv(TOKEN_ENV_VAR) != null) {
            token = System.getenv(TOKEN_ENV_VAR);
        } else {
            logger.error("Missing a token");
            return;
        }

        final String CHANNEL_FILTER_ENV_VAR = "CHANNEL_FILTER";
        List<BigInteger> channelFilter = null;
        String chanFilter = null;
        if (args.length >= 2) {
            chanFilter = args[1];
        } else if (System.getenv(CHANNEL_FILTER_ENV_VAR) != null) {
            chanFilter = System.getenv(CHANNEL_FILTER_ENV_VAR);
        }
        if (chanFilter != null) {
            channelFilter = Arrays.asList(chanFilter.split(","))
                .stream()
                .map(s -> new BigInteger(s))
                .toList();
            logger.debug("Filtering on " + channelFilter);
        }

        final IChannelDataManager dataManager;
        try {
            dataManager = new SQLiteDataManager("ppp.db", logger);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return;
        }

        final var commands = Map.of(
            "times", new TimesCommand(),
            "stats", new StatsCommand(),
            "troll", new TrollCommand());
        final CommandParser commandParser = new CommandParser(commands);
        
        final List<IParser> parsers = List.of(
                new MiniCrosswordTimeParser(),
                new WordleResultParser(logger),
                commandParser);

        final List<ITask> tasks = List.of(
            new MiniResultsForDateTask(logger),
            new MiniResultsForWeekTask(logger),
            new MiniEloTask(logger));

        final DiscordPPPBot bot = new DiscordPPPBot(token, parsers, tasks, channelFilter, dataManager, logger);

        bot.login();
        bot.startListening();
    }
}
