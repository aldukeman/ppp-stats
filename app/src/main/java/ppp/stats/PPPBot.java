package ppp.stats;

import java.util.HashMap;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import ppp.stats.data.IDataManager;
import ppp.stats.data.InMemoryDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.processor.CommandProcessor;
import ppp.stats.processor.IProcessor;
import ppp.stats.processor.MiniCrosswordTimeProcessor;
import ppp.stats.processor.commands.ICommandHandler;
import ppp.stats.processor.commands.TimesCommandHandler;

public class PPPBot {
    final private DiscordClient client;
    private GatewayDiscordClient gateway;
    final private ILogger logger = new SystemOutLogger();
    final private IProcessor[] processors;
    private long currentMaxId = 0;

    public PPPBot(String token, IProcessor[] processors) {
        this.client = DiscordClient.create(token);
        this.processors = processors;
    }

    public void login() {
        this.gateway = this.client.login().block();
    }

    public void startListening() {
        this.gateway.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(message -> message.getChannel().block() instanceof TextChannel)
            .filter(message -> {
                String chanName = ((TextChannel)message.getChannel().block()).getName();
                return chanName.equals("periodic-perplexing-parlances") || chanName.equals("ppp-stats-test"); })
            .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
            .filter(message -> message.getId().asLong() > this.currentMaxId)
            .subscribe(message -> {
                this.currentMaxId = message.getId().asLong();
                this.processMessage(message);
            });
        this.gateway.onDisconnect().block();
    }

    private boolean processMessage(Message msg) {
        this.logger.trace("Received message: " + msg.getContent());

        for(IProcessor proc: this.processors) {
            if(proc.process(msg)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Missing a token");
            return;
        }

        final IDataManager dataManager = new InMemoryDataManager();
        final String token = args[0];
        final MiniCrosswordTimeProcessor timeProcessor = new MiniCrosswordTimeProcessor(dataManager);

        final HashMap<String, ICommandHandler> commands = new HashMap<>();
        commands.put("times", new TimesCommandHandler(dataManager));
        final CommandProcessor commandProcessor = new CommandProcessor(commands);
        final IProcessor[] processors = { timeProcessor, commandProcessor };
        final PPPBot bot = new PPPBot(token, processors);

        bot.login();
        bot.startListening();
    }
}
