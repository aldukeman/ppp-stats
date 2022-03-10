package ppp.stats.parser;

import java.util.List;

import ppp.stats.action.IAction;
import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel;

public interface IParser {
    IAction parse(IMessage message);
    List<ITextChannel.Type> supportedChannelTypes();
}
