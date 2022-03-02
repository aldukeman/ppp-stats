package ppp.stats.messenger.message;

import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;

public class BasicMessage implements IBotMessage {
    public final String msgString;

    public BasicMessage(String msgString) {
        this.msgString = msgString;
    }

    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        msgClient.sendBasicMessage(channel, this);
    }
}