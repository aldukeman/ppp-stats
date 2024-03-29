package ppp.stats.messenger;

import ppp.stats.messenger.message.ReactToMessage;
import ppp.stats.messenger.message.BasicMessage;
import ppp.stats.messenger.message.MiniEloMessage;
import ppp.stats.messenger.message.MiniResultsForDateIntervalMessage;
import ppp.stats.messenger.message.MiniResultsForDateMessage;
import ppp.stats.messenger.message.UserMiniStatsMessage;
import ppp.stats.messenger.message.UserMiniTimesMessage;
import ppp.stats.models.ITextChannel;

public interface IMessageClient {
    public void sendBasicMessage(ITextChannel channel, BasicMessage msg);
    public void sendUserMiniTimes(ITextChannel channel, UserMiniTimesMessage msg);
    public void sendUserMiniStats(ITextChannel channel, UserMiniStatsMessage msg);
    public void sendMiniResultsForDate(ITextChannel channel, MiniResultsForDateMessage msg);
    public void sendMiniResultsForDateInterval(ITextChannel channel, MiniResultsForDateIntervalMessage msg);
    public void sendMessageReaction(ITextChannel channel, ReactToMessage msg);
    public void sendMiniEloResults(ITextChannel channel, MiniEloMessage msg);

    default String timeString(Integer time) {
        int seconds = time.intValue();
        int min = seconds / 60;
        seconds = seconds % 60;
        return min + ":" + String.format("%02d", seconds);
    }

    default String timeString(Float time) {
        int min = time.intValue() / 60;
        float seconds = time.floatValue() % 60;
        return min + ":" + String.format("%04.1f", seconds);
    }
}
