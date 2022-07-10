package ppp.stats.bot;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ChannelData;
import discord4j.discordjson.json.UserGuildData;
import ppp.stats.data.IChannelDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.messenger.DiscordMessageClient;
import ppp.stats.models.DiscordMessage;
import ppp.stats.models.DiscordTextChannel;
import ppp.stats.models.DiscordGuildChannel;
import ppp.stats.models.ITextChannel;
import ppp.stats.parser.IParser;
import ppp.stats.task.ITask;

public class DiscordPPPBot extends PPPBot {
    final private DiscordClient client;
    private GatewayDiscordClient gateway;
    final private List<String> channelFilter;
    final private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1);
    final private Map<Long, DiscordTextChannel> channelMap = new Hashtable<>();

    public DiscordPPPBot(String token, List<IParser> parsers, List<ITask> tasks, List<String> channelFilter,
            IChannelDataManager dataManager, ILogger logger) {
        super(logger, parsers, tasks, dataManager);

        this.client = DiscordClient.create(token);
        this.channelFilter = channelFilter;

    }

    public void login() {
        this.gateway = this.client.login().block();
        this.msgClient = new DiscordMessageClient(this.gateway, this.logger);

        List<UserGuildData> guilds = this.client.getGuilds().collectList().block();
        for (UserGuildData guild : guilds) {
            List<ChannelData> channels = this.client.getGuildById(Snowflake.of(guild.id())).getChannels().collectList()
                    .block();
            for (ChannelData channel : channels) {
                if (channel.name() != null && this.channelFilter.contains(channel.name().get())) {
                    this.channel = new DiscordGuildChannel(
                            (TextChannel) this.gateway.getChannelById(Snowflake.of(channel.id())).block());
                }
            }
        }
    }

    @Override
    public void startListening(MessageProcessor processor) {
        this.gateway.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> {
                    if (this.channelFilter == null) {
                        return true;
                    }
                    if (message.getChannel().block() instanceof TextChannel) {
                        String chanName = ((TextChannel) message.getChannel().block()).getName();
                        return this.channelFilter.contains(chanName);
                    }
                    return true;
                })
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .subscribe(message -> {
                    DiscordMessage msg = this.message(message);
                    if (msg != null) {
                        processor.process(msg);
                    }
                });

        this.gateway.onDisconnect().block();
    }

    private DiscordMessage message(Message message) {
        DiscordTextChannel channel = this.channelForId(message.getChannel().block().getId().asLong());
        return new DiscordMessage(message, channel);
    }

    private DiscordTextChannel channelForId(long id) {
        DiscordTextChannel ret = this.channelMap.get(Long.valueOf(id));
        if (ret == null) {
            ret = DiscordTextChannel.from(this.gateway.getChannelById(Snowflake.of(id)).block());
            if (ret != null) {
                this.channelMap.put(Long.valueOf(id), ret);
            }
        }
        return ret;
    }
}
