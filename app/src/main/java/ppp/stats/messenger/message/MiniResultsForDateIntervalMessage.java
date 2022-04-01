package ppp.stats.messenger.message;

import java.time.LocalDate;
import java.util.List;

import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;
import ppp.stats.utility.Pair;

public class MiniResultsForDateIntervalMessage implements IBotMessage {
    public final LocalDate start;
    public final LocalDate end;
    public final List<Pair<String, Float>> rows;

    public MiniResultsForDateIntervalMessage(LocalDate start, LocalDate end, List<Pair<String, Float>> rows) {
        this.start = start;
        this.end = end;
        this.rows = rows;
    }

    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        msgClient.sendMiniResultsForDateInterval(channel, this);
    }
}
