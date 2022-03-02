package ppp.stats.messenger.message;

import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;

public class NullMessage implements IBotMessage {
    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        // no-op
    }
}
