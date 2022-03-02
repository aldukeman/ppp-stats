package ppp.stats.action;

import ppp.stats.data.IDataManager;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.NullMessage;
import ppp.stats.models.IMessage;

public class NullAction implements IAction {
    @Override
    public IBotMessage process(IMessage message, IDataManager dataManager) {
        return new NullMessage();
    }
}
