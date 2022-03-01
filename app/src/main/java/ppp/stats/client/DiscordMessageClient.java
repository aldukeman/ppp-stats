package ppp.stats.client;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import ppp.stats.logging.ILogger;
import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel;
import ppp.stats.models.IUser;

public class DiscordMessageClient implements IMessageClient {
    private GatewayDiscordClient client;
    private ILogger logger;

    public DiscordMessageClient(GatewayDiscordClient client, ILogger logger) {
        this.client = client;
        this.logger = logger;
    }

    private MessageChannel messageChannel(ITextChannel channel) {
        Channel chan = this.client.getChannelById(Snowflake.of(channel.getId())).block();
        if (chan == null || !(chan instanceof MessageChannel)) {
            this.logger.error("Invalid channel");
            return null;
        }
        return (MessageChannel) chan;
    }

    private Message message(ITextChannel textChannel, IMessage message) {
        return this.client.getMessageById(Snowflake.of(textChannel.getId()), Snowflake.of(message.getId())).block();
    }

    @Override
    public void sendString(ITextChannel channel, BotMessage.String msg) {
        MessageChannel msgChannel = this.messageChannel(channel);
        if (msgChannel == null) {
            return;
        }

        msgChannel.createMessage(msg.msgString).block();
    }

    @Override
    public void sendMiniScores(ITextChannel channel, BotMessage.UserMiniTimes msg) {
        MessageChannel msgChannel = this.messageChannel(channel);
        if (msgChannel == null) {
            return;
        }

        String resp;
        if (msg.timesMap == null || msg.timesMap.size() == 0) {
            resp = "No times have been recorded for " + msg.requestor.getUsername();
            msgChannel.createMessage(resp).block();
        } else {
            List<LocalDate> dates = msg.timesMap.keySet().stream().sorted((e1, e2) -> e1.compareTo(e2)).toList();
            List<String> dateStrings = dates.stream().map(d -> d.toString()).toList();
            String dateString = String.join("\n", dateStrings);

            List<String> timeStrings = dates
                    .stream()
                    .map(d -> msg.timesMap.get(d))
                    .map(t -> this.timeString(t))
                    .toList();
            String timeString = String.join("\n", timeStrings);

            EmbedCreateSpec spec = EmbedCreateSpec.builder()
                    .addField("Date", dateString, true)
                    .addField("Time", timeString, true)
                    .build();

            msgChannel.createMessage(spec).block();
        }
    }

    @Override
    public void sendMiniStats(ITextChannel channel, BotMessage.UserMiniTimesStats msg) {
        MessageChannel msgChannel = this.messageChannel(channel);
        if (msgChannel == null) {
            return;
        }

        IUser requestor = msg.requestor;
        Map<LocalDate, Integer> timesMap = msg.timesMap;

        int numEntries = timesMap.size();
        if (numEntries < 2) {
            this.sendString(channel, new BotMessage.String("Not enough entries for user " + requestor.getUsername()));
            return;
        }

        LocalDate earliest = timesMap.keySet().stream().min((d_1, d_2) -> d_1.compareTo(d_2)).get();
        LocalDate latest = timesMap.keySet().stream().min((d_1, d_2) -> d_2.compareTo(d_1)).get();

        List<Integer> times = timesMap.values().stream().sorted().toList();
        int min = times.get(0).intValue();
        float median;
        if(times.size() % 2 == 0) {
            int lowerIdx = times.size() / 2 - 1;
            median = (times.get(lowerIdx).floatValue() + times.get(lowerIdx + 1).floatValue()) / 2;
        } else {
            median = times.get(times.size() / 2);
        }
        float average = times.stream().reduce(0, (a, b) -> a.intValue() + b.intValue()) / (float) numEntries;
        int max = times.stream().min((a, b) -> b.intValue() - a.intValue()).get();

        String resp = "```" +
                "Found " + numEntries + " entries from " + earliest + " to " + latest + "\n" +
                "Min: " + this.timeString(min) + "\n" +
                "Median: " + this.timeString(median) + "\n" +
                "Average: " + this.timeString(average) + "\n" +
                "Max: " + this.timeString(max) + "\n" +
                "```";

        msgChannel.createMessage(resp).block();
    }

    @Override
    public void acknowledgeMessage(ITextChannel channel, BotMessage.AcknowledgeMessage msg) {
        Message message = this.message(channel, msg.message);
        if (message == null) {
            return;
        }

        message.addReaction(ReactionEmoji.unicode("🤖")).block();
    }
}
