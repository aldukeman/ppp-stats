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
import ppp.stats.bot.IBot;
import ppp.stats.client.DiscordMessageClient;
import ppp.stats.client.IMessageClient;
import ppp.stats.data.IDataManager;
import ppp.stats.data.SQLiteDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.models.DiscordMessage;
import ppp.stats.processor.CommandProcessor;
import ppp.stats.processor.IProcessor;
import ppp.stats.processor.MiniCrosswordTimeProcessor;
import ppp.stats.processor.commands.ICommandHandler;
import ppp.stats.processor.commands.StatsCommandHandler;
import ppp.stats.processor.commands.TimesCommandHandler;

public class PPPBot implements IBot {
    final private DiscordClient client;
    private GatewayDiscordClient gateway;
    final private ILogger logger = new SystemOutLogger();
    final private IProcessor[] processors;
    final private List<String> channelFilter;
    private IMessageClient msgClient;

    public PPPBot(String token, IProcessor[] processors, List<String> channelFilter) {
        this.client = DiscordClient.create(token);
        this.processors = processors;
        this.channelFilter = channelFilter;
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
                if(this.channelFilter == null) { return true; }
                String chanName = ((TextChannel)message.getChannel().block()).getName();
                return this.channelFilter.contains(chanName); })
            .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
            .subscribe(message -> {
                this.processMessage(message);
            });

        this.gateway.onDisconnect().block();
    }

    private boolean processMessage(Message msg) {
        this.logger.trace("Received message: " + msg.getContent());

        for(IProcessor proc: this.processors) {
            if(proc.process(new DiscordMessage(msg), this.msgClient)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        ILogger logger = new SystemOutLogger();

        final String TOKEN_ENV_VAR = "DISCORD_BOT_TOKEN";
        final String token;
        if(args.length >= 1) {
            token = args[0];
        } else if(System.getenv(TOKEN_ENV_VAR) != null) {
            token = System.getenv(TOKEN_ENV_VAR);
        } else {
            System.out.println("Missing a token");
            return;
        }

        final String CHANNEL_FILTER_ENV_VAR = "CHANNEL_FILTER";
        List<String> channelFilter = null;
        String chanFilter = null;
        if(args.length >= 2) {
            chanFilter = args[1];
        } else if(System.getenv(CHANNEL_FILTER_ENV_VAR) != null) {
            chanFilter = System.getenv(CHANNEL_FILTER_ENV_VAR);
        }
        if(chanFilter != null) {
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

        final MiniCrosswordTimeProcessor timeProcessor = new MiniCrosswordTimeProcessor(dataManager, logger);
        final HashMap<String, ICommandHandler> commands = new HashMap<>();
        commands.put("times", new TimesCommandHandler(dataManager, logger));
        commands.put("stats", new StatsCommandHandler(dataManager, logger));
        final CommandProcessor commandProcessor = new CommandProcessor(commands, logger);
        final IProcessor[] processors = { timeProcessor, commandProcessor };

        final PPPBot bot = new PPPBot(token, processors, channelFilter);

        bot.login();
        bot.startListening();
    }
}
