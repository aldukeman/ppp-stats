package ppp.stats.messenger.message;

import java.time.LocalDate;
import java.util.Map;

import ppp.stats.data.model.MiniTimeMessageModel;
import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;
import ppp.stats.models.IUser;

public class UserMiniStatsMessage implements IBotMessage {
    public final IUser requestor;
    public final Map<LocalDate, MiniTimeMessageModel> timesMap;

    public UserMiniStatsMessage(IUser requestor, Map<LocalDate, MiniTimeMessageModel> timesMap) {
        this.requestor = requestor;
        this.timesMap = timesMap;
    }

    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        msgClient.sendUserMiniStats(channel, this);
    }
}
