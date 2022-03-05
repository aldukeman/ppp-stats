package ppp.stats.messenger.message;

import java.time.LocalDate;
import java.util.List;

import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;
import ppp.stats.utility.Pair;

public class MiniResultsForDateMessage implements IBotMessage {
    public final LocalDate date;
    public final List<Pair<String, Integer>> rows;

    public MiniResultsForDateMessage(LocalDate date, List<Pair<String, Integer>> rows) {
        this.date = date;
        this.rows = rows;
    }

    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        msgClient.sendMiniResultsForDate(channel, this);
    }
}
