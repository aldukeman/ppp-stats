package ppp.stats.messenger.message;

import java.util.List;

import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;
import ppp.stats.utility.Pair;

public class MiniEloMessage implements IBotMessage {
    public final List<Pair<String, Integer>> rows;

    public MiniEloMessage(List<Pair<String, Integer>> rows) {
        this.rows = rows;
    }

    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        msgClient.sendMiniEloResults(channel, this);
    }
}
