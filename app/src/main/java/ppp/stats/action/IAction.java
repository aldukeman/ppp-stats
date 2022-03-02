package ppp.stats.action;

import ppp.stats.data.IDataManager;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.models.IMessage;

public interface IAction {
    IBotMessage process(IMessage message, IDataManager dataManager);
}
