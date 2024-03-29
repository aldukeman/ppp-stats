package ppp.stats.action;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.ReactToMessage;
import ppp.stats.models.IMessage;
import ppp.stats.models.IUser;

public class ProcessMiniTimeAction implements IAction {
    private final IUser user;
    private final Integer time;
    private final ILogger logger;

    public ProcessMiniTimeAction(IUser user, Integer time) {
        this(user, time, SystemOutLogger.shared);
    }

    public ProcessMiniTimeAction(IUser user, Integer time, ILogger logger) {
        this.user = user;
        this.time = time;
        this.logger = logger;
    }
    
    @Override
    public IBotMessage process(IMessage message, IChannelDataManager dataManager) {
        this.logger.trace("Adding mini time for user " + user.getId());
        dataManager.setUserName(user.getId(), user.getUsername());
        dataManager.addUserTime(user.getId(), IChannelDataManager.MiniDate(), this.time.intValue(), message.getId());
        return new ReactToMessage(message.getId(), "🤖");
    }
}
