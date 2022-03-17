package ppp.stats.messenger.message;

import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;

public class ReactToMessage implements IBotMessage {
    public final long msgId;
    public final String reaction;

    public ReactToMessage(long msgId, String reaction) {
        this.msgId = msgId;
        this.reaction = reaction;
    }

    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        msgClient.sendMessageReaction(channel, this);
    }
}
