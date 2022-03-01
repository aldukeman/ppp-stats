package ppp.stats.processor.commands;

import ppp.stats.client.IMessageClient;
import ppp.stats.models.IMessage;

public interface ICommandHandler {
    void process(IMessage msg, IMessageClient msgClient);
}
