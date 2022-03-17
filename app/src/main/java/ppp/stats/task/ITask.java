package ppp.stats.task;

import java.time.LocalDateTime;
import java.util.List;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.messenger.message.IBotMessage;

public interface ITask {
    List<IBotMessage> execute(IChannelDataManager dataManager);
    LocalDateTime nextExecutionDateTime();
}
