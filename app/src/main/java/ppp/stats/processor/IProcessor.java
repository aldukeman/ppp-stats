package ppp.stats.processor;

import ppp.stats.client.IMessageClient;
import ppp.stats.models.IMessage;

public interface IProcessor {
    boolean process(IMessage msg, IMessageClient msgClient);
}
