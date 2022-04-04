package ppp.stats.action;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.messenger.message.BasicMessage;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.models.IMessage;

public class SendWordleStatsAction implements IAction {
    private final ILogger logger = SystemOutLogger.shared;
    
    @Override
    public IBotMessage process(IMessage message, IChannelDataManager dataManager) {
        return new BasicMessage("'Not implemented'");
    }

    
}
