package ppp.stats.task;

import java.time.LocalDateTime;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.messenger.message.IBotMessage;

public interface ITask {
    IBotMessage execute(IChannelDataManager dataManager);
    LocalDateTime nextExecutionDateTime();
}
