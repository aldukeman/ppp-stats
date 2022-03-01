package ppp.stats.client;

import ppp.stats.client.BotMessage.AcknowledgeMessage;
import ppp.stats.client.BotMessage.String;
import ppp.stats.client.BotMessage.UserMiniTimes;
import ppp.stats.client.BotMessage.UserMiniTimesStats;
import ppp.stats.models.ITextChannel;

public class IMessageClientMock implements IMessageClient {
    @Override
    public void sendString(ITextChannel channel, String msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void sendMiniScores(ITextChannel channel, UserMiniTimes msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void sendMiniStats(ITextChannel channel, UserMiniTimesStats msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void acknowledgeMessage(ITextChannel channel, AcknowledgeMessage msg) {
        // TODO Auto-generated method stub
    }
}
