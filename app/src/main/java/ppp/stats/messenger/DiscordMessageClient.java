package ppp.stats.messenger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import ppp.stats.logging.ILogger;
import ppp.stats.messenger.message.AcknowledgeMessage;
import ppp.stats.messenger.message.BasicMessage;
import ppp.stats.messenger.message.MiniResultsForDateMessage;
import ppp.stats.messenger.message.UserMiniStatsMessage;
import ppp.stats.messenger.message.UserMiniTimesMessage;
import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel;
import ppp.stats.models.IUser;
import ppp.stats.utility.Pair;

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
    public void sendBasicMessage(ITextChannel channel, BasicMessage msg) {
        MessageChannel msgChannel = this.messageChannel(channel);
        if (msgChannel == null) {
            return;
        }

        msgChannel.createMessage(msg.msgString).block();
    }

    @Override
    public void sendUserMiniTimes(ITextChannel channel, UserMiniTimesMessage msg) {
        MessageChannel msgChannel = this.messageChannel(channel);
        if (msgChannel == null) {
            return;
        }

        if (msg.timesMap == null || msg.timesMap.size() == 0) {
            String resp = "No times have been recorded for " + msg.requestor.getUsername();
            msgChannel.createMessage(resp).block();
            return;
        }

        int numRows = Integer.min(msg.numRowsToSend, msg.timesMap.size());
        List<Pair<String, String>> rowPairs = msg.timesMap
                .entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .toList()
                .subList(0, numRows)
                .stream()
                .map(e -> new Pair<String, String>(e.getKey().toString(), this.timeString(e.getValue())))
                .toList();

        int dateHeaderLength = rowPairs
                .stream()
                .map(e -> e.first.length())
                .max((a, b) -> a - b)
                .get();
        int timeHeaderLength = rowPairs
                .stream()
                .map(e -> e.second.length())
                .max((a, b) -> a - b)
                .get();

        dateHeaderLength = Integer.max(dateHeaderLength + 2, "Date  ".length());
        timeHeaderLength = Integer.max(timeHeaderLength + 2, "  Time".length());

        String header = String.format("%-" + dateHeaderLength + "s|%" + timeHeaderLength + "s", "Date", "Time");
        String splitter = new String(new char[dateHeaderLength]).replace('\u0000', '-') +
                "+" +
                new String(new char[timeHeaderLength]).replace('\u0000', '-');

        List<String> rows = new ArrayList<>(numRows);
        for (Pair<String, String> entry : rowPairs) {
            rows.add(String.format("%-" + dateHeaderLength + "s|" + "%" + timeHeaderLength + "s",
                    entry.first,
                    entry.second));
        }

        List<String> output = new ArrayList<>(rows.size() + 4);
        output.add(header);
        output.add(splitter);
        output.addAll(rows);

        String message = "```" + String.join("\n", output);
        int extraAmount = msg.timesMap.size() - numRows;
        if (extraAmount > 0) {
            message += "\n\n" + String.format("...%s more entries", extraAmount);
        }

        message += "```";
        msgChannel.createMessage(message).block();
    }

    @Override
    public void sendUserMiniStats(ITextChannel channel, UserMiniStatsMessage msg) {
        MessageChannel msgChannel = this.messageChannel(channel);
        if (msgChannel == null) {
            this.sendBasicMessage(channel, new BasicMessage("Error: invalid channel"));
            return;
        }

        IUser requestor = msg.requestor;
        Map<LocalDate, Integer> timesMap = msg.timesMap;

        int numEntries = timesMap.size();
        if (numEntries < 1) {
            this.sendBasicMessage(channel, new BasicMessage("Not enough entries for user " + requestor.getUsername()));
            return;
        }

        LocalDate earliest = timesMap.keySet().stream().min((d_1, d_2) -> d_1.compareTo(d_2)).get();
        LocalDate latest = timesMap.keySet().stream().min((d_1, d_2) -> d_2.compareTo(d_1)).get();

        List<Integer> times = timesMap.values().stream().sorted().toList();
        int min = times.get(0).intValue();
        float median;
        if (times.size() % 2 == 0) {
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
    public void sendMiniResultsForDate(ITextChannel channel, MiniResultsForDateMessage msg) {
        MessageChannel msgChannel = this.messageChannel(channel);
        if (msgChannel == null) {
            this.sendBasicMessage(channel, new BasicMessage("Error: invalid channel"));
            return;
        }
        if (msg.rows.size() == 0) {
            this.logger.error("No data rows to send");
            return;
        }

        List<Pair<String, Integer>> rowData = msg.rows;
        String winnersStr = this.makeWinnersString(rowData);

        int nameColLength = rowData.stream()
                .map(e -> e.first.length())
                .max((a, b) -> a - b)
                .get()
                .intValue();
        nameColLength += 5;

        String headers = String.format("%-" + nameColLength + "s | Time", "Name");
        String resp = "**Mini crossword results for " + msg.date + "**\n" +
                "Congratulations " + winnersStr + " for getting the top score!\n" +
                "```\n" +
                headers + "\n";
        String splitter = new String(new char[nameColLength + 1]).replace('\u0000', '-') + "+--------";
        resp += splitter + "\n";

        List<String> rows = new ArrayList<>();
        for (var entry : rowData) {
            rows.add(String.format("%-" + nameColLength + "s | " + this.timeString(entry.second), entry.first));
        }

        resp += String.join("\n", rows);
        resp += "```";

        msgChannel.createMessage(resp).block();
    }

    public String makeWinnersString(List<Pair<String, Integer>> rows) {
        final int max = rows.get(0).second.intValue();
        List<String> winners = new ArrayList<>();
        winners.add(rows.get(0).first);
        for(int i = 1; i < rows.size(); ++i) {
            Pair<String, Integer> next = rows.get(i);
            if(max == next.second.intValue()) {
                winners.add(next.first);
            } else {
                break;
            }
        }

        String winnersString;
        if(winners.size() > 1) {
            winners.set(winners.size() - 1, "and " + winners.get(winners.size() - 1));
            if(winners.size() > 2) {
                winnersString = String.join(", ", winners);
            } else {
                winnersString = winners.get(0) + " " + winners.get(1);
            }
        } else {
            winnersString = winners.get(0);
        }

        return winnersString;
    }

    @Override
    public void sendMessageAcknowledgement(ITextChannel channel, AcknowledgeMessage msg) {
        Message message = this.message(channel, msg.message);
        if (message == null) {
            return;
        }

        message.addReaction(ReactionEmoji.unicode("🤖")).block();
    }
}
