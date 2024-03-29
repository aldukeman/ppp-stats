package ppp.stats.messenger.message;

import java.time.LocalDate;
import java.util.Map;

import ppp.stats.data.model.MiniTimeMessageModel;
import ppp.stats.messenger.IMessageClient;
import ppp.stats.models.ITextChannel;
import ppp.stats.models.IUser;

public class UserMiniTimesMessage implements IBotMessage {
    public final IUser requestor;
    public final Map<LocalDate, MiniTimeMessageModel> timesMap;
    public final int numRowsToSend;

    public UserMiniTimesMessage(IUser requestor, Map<LocalDate, MiniTimeMessageModel> timesMap) {
        this(requestor, timesMap, timesMap.size());
    }

    public UserMiniTimesMessage(IUser requestor, Map<LocalDate, MiniTimeMessageModel> timesMap, int numRowsToSend) {
        this.requestor = requestor;
        this.timesMap = timesMap;
        this.numRowsToSend = numRowsToSend;
    }

    @Override
    public void send(IMessageClient msgClient, ITextChannel channel) {
        msgClient.sendUserMiniTimes(channel, this);
    }
}
