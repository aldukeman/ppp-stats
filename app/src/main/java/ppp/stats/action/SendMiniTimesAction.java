package ppp.stats.action;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.messenger.message.BasicMessage;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.UserMiniTimesMessage;
import ppp.stats.models.IMessage;
import ppp.stats.models.IUser;

public class SendMiniTimesAction implements IAction {
    private final ILogger logger = SystemOutLogger.shared;
    private final int maxRows;

    public SendMiniTimesAction() {
        this(Integer.MAX_VALUE);
    }

    public SendMiniTimesAction(int maxRows) {
        this.maxRows = maxRows;
    }

    @Override
    public IBotMessage process(IMessage message, IChannelDataManager dataManager) {
        IUser requestor = message.getAuthor();
        if (requestor != null) {
            var dict = dataManager.getTimesForUserId(requestor.getId());
            return new UserMiniTimesMessage(requestor, dict, this.maxRows);
        } else {
            this.logger.debug("Null author");
            return new BasicMessage("`Error: invalid user");
        }
    }
}
