package ppp.stats.parser;

import ppp.stats.action.IAction;
import ppp.stats.models.IMessage;

public interface IParser {
    IAction parse(IMessage message);
}
