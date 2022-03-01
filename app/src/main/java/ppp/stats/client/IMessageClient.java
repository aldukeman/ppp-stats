package ppp.stats.client;

import ppp.stats.models.ITextChannel;

public interface IMessageClient {
    public void sendString(ITextChannel channel, BotMessage.String msg);
    public void sendMiniScores(ITextChannel channel, BotMessage.UserMiniTimes msg);
    public void sendMiniStats(ITextChannel channel, BotMessage.UserMiniTimesStats msg);
    public void acknowledgeMessage(ITextChannel channel, BotMessage.AcknowledgeMessage msg);

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
