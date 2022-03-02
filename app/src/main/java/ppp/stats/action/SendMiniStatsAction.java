package ppp.stats.action;

import java.time.LocalDate;
import java.util.Map;

import ppp.stats.data.IDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.messenger.message.BasicMessage;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.UserMiniStatsMessage;
import ppp.stats.models.IMessage;
import ppp.stats.models.IUser;

public class SendMiniStatsAction implements IAction {
    private final ILogger logger = SystemOutLogger.shared;
    
    @Override
    public IBotMessage process(IMessage message, IDataManager dataManager) {
        IUser requestor = message.getAuthor();
        if (requestor != null) {
            Map<LocalDate, Integer> dict = dataManager.getTimesForUserId(requestor.getId());
            return new UserMiniStatsMessage(requestor, dict);
        } else {
            this.logger.debug("Null author");
            return new BasicMessage("`Error: invalid user");
        }
    }
}
