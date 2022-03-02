package ppp.stats.messenger.message;

import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;

public interface IBotMessage {
    public void send(IMessageClient msgClient, ITextChannel channel);
}