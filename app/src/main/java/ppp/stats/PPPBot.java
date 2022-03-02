package ppp.stats;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import ppp.stats.action.IAction;
import ppp.stats.bot.IBot;
import ppp.stats.data.IDataManager;
import ppp.stats.data.SQLiteDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.messenger.DiscordMessageClient;
import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.DiscordMessage;
import ppp.stats.models.IMessage;
import ppp.stats.parser.CommandParser;
import ppp.stats.parser.IParser;
import ppp.stats.parser.MiniCrosswordTimeParser;
import ppp.stats.parser.command.ICommand;
import ppp.stats.parser.command.StatsCommand;
import ppp.stats.parser.command.TimesCommand;

public class PPPBot implements IBot {
    final private DiscordClient client;
    private GatewayDiscordClient gateway;
    final private ILogger logger = new SystemOutLogger();
    final private IParser[] parsers;
    final private List<String> channelFilter;
    private IMessageClient msgClient;
    final private IDataManager dataManager;

    public PPPBot(String token, IParser[] parsers, List<String> channelFilter, IDataManager dataManager) {
        this.client = DiscordClient.create(token);
        this.parsers = parsers;
        this.channelFilter = channelFilter;
        this.dataManager = dataManager;
    }

    public void login() {
        this.gateway = this.client.login().block();
        this.msgClient = new DiscordMessageClient(this.gateway, this.logger);
    }

    public void startListening() {
        this.gateway.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getChannel().block() instanceof TextChannel)
                .filter(message -> {
                    if (this.channelFilter == null) {
                        return true;
                    }
                    String chanName = ((TextChannel) message.getChannel().block()).getName();
                    return this.channelFilter.contains(chanName);
                })
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .subscribe(message -> {
                    this.processMessage(message);
                });

        this.gateway.onDisconnect().block();
    }

    private boolean processMessage(Message msg) {
        this.logger.trace("Received message: " + msg.getContent());

        IMessage message = new DiscordMessage(msg);
        for (IParser parser : this.parsers) {
            IAction action = parser.parse(message);
            if (action != null) {
                action.process(message, this.dataManager)
                        .send(this.msgClient, message.getChannel());
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        ILogger logger = new SystemOutLogger();

        final String TOKEN_ENV_VAR = "DISCORD_BOT_TOKEN";
        final String token;
        if (args.length >= 1) {
            token = args[0];
        } else if (System.getenv(TOKEN_ENV_VAR) != null) {
            token = System.getenv(TOKEN_ENV_VAR);
        } else {
            System.out.println("Missing a token");
            return;
        }

        final String CHANNEL_FILTER_ENV_VAR = "CHANNEL_FILTER";
        List<String> channelFilter = null;
        String chanFilter = null;
        if (args.length >= 2) {
            chanFilter = args[1];
        } else if (System.getenv(CHANNEL_FILTER_ENV_VAR) != null) {
            chanFilter = System.getenv(CHANNEL_FILTER_ENV_VAR);
        }
        if (chanFilter != null) {
            channelFilter = Arrays.asList(chanFilter.split(","));
            logger.debug("Filtering on " + channelFilter);
        }

        final IDataManager dataManager;
        try {
            dataManager = new SQLiteDataManager("ppp.db", logger);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return;
        }

        final HashMap<String, ICommand> commands = new HashMap<>();
        commands.put("times", new TimesCommand());
        commands.put("stats", new StatsCommand());
        final CommandParser commandParser = new CommandParser(commands);
        final IParser[] processors = { new MiniCrosswordTimeParser(), commandParser };

        final PPPBot bot = new PPPBot(token, processors, channelFilter, dataManager);

        bot.login();
        bot.startListening();
    }
}
