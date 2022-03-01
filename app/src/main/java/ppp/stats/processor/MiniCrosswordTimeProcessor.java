package ppp.stats.processor;

import ppp.stats.client.BotMessage;
import ppp.stats.client.IMessageClient;
import ppp.stats.data.IDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.models.IMessage;
import ppp.stats.models.IUser;
import ppp.stats.parser.MiniCrosswordTimeParser;

public class MiniCrosswordTimeProcessor implements IProcessor {
    final private MiniCrosswordTimeParser parser = new MiniCrosswordTimeParser();
    final private IDataManager dataManager;
    final private ILogger logger;

    public MiniCrosswordTimeProcessor(IDataManager dataManager, ILogger logger) {
        this.dataManager = dataManager;
        this.logger = logger;
    }

    @Override
    public boolean process(IMessage msg, IMessageClient msgClient) {
        Integer time = this.parser.getTime(msg.getContent());
        if(time != null) {
            IUser user = msg.getAuthor();
            if(user != null) {
                long id = user.getId();
                this.dataManager.addUserTime(id, time.intValue());
                this.dataManager.setUserName(id, user.getUsername());
                msgClient.acknowledgeMessage(msg.getChannel(), new BotMessage.AcknowledgeMessage(msg));
                this.logger.trace("Processed time: " + time + ", id: " + id);
                return true;
            } else {
                this.logger.error("Found a valid message, but no associated user");
            }
        }
        return false;
    }
}
