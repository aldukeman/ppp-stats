package ppp.stats.messenger.message;

import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel;

public class AcknowledgeMessage implements IBotMessage {
    public final IMessage message;

    public AcknowledgeMessage(IMessage message) {
        this.message = message;
    }

    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        msgClient.sendMessageAcknowledgement(channel, this);
    }
}
