package ppp.stats.processor.commands;

import java.time.LocalDate;
import java.util.Map;

import ppp.stats.client.BotMessage;
import ppp.stats.client.IMessageClient;
import ppp.stats.data.IDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.models.IMessage;
import ppp.stats.models.IUser;

public class StatsCommandHandler implements ICommandHandler {
    final private IDataManager dataManager;
    final private ILogger logger;

    public StatsCommandHandler(IDataManager dataManager, ILogger logger) {
        this.dataManager = dataManager;
        this.logger = logger;
    }

    @Override
    public void process(IMessage msg, IMessageClient msgClient) {
        IUser author = msg.getAuthor();
        if (author != null) {
            Map<LocalDate, Integer> dict = this.dataManager.getTimesForUserId(author.getId());
            BotMessage.UserMiniTimesStats timesMsg = new BotMessage.UserMiniTimesStats(author, dict);
            msgClient.sendMiniStats(msg.getChannel(), timesMsg);
        } else {
            this.logger.debug("Null author");
        }
    }
}
