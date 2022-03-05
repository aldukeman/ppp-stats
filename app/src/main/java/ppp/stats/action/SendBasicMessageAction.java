package ppp.stats.action;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.messenger.message.BasicMessage;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.models.IMessage;

public class SendBasicMessageAction implements IAction {
    private final String message;

    public SendBasicMessageAction(String message) {
        this.message = message;
    }

    @Override
    public IBotMessage process(IMessage message, IChannelDataManager dataManager) {
        return new BasicMessage(this.message);
    }
    
}
