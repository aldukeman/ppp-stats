package ppp.stats.processor;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import ppp.stats.data.IDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.SystemOutLogger;
import ppp.stats.models.DiscordUser;
import ppp.stats.parser.MiniCrosswordTimeParser;

public class MiniCrosswordTimeProcessor implements IProcessor {
    private IDataManager dataManager;
    private MiniCrosswordTimeParser parser = new MiniCrosswordTimeParser();
    private ILogger logger = new SystemOutLogger();

    public MiniCrosswordTimeProcessor(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean process(Message msg) {
        Integer time = this.parser.getTime(msg.getContent());
        if(time != null) {
            User user = msg.getAuthor().get();
            if(user != null) {
                DiscordUser dUser = new DiscordUser(user);
                this.dataManager.addUserTime(dUser.getId(), time.intValue());
                this.dataManager.setUserName(dUser.getId(), user.getUsername());
                return true;
            } else {
                this.logger.error("Found a valid message, but no associated user");
            }
        }
        return false;
    }
    
}
