package ppp.stats.client;

import java.time.LocalDate;
import java.util.Map;

import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel;
import ppp.stats.models.IUser;

public interface BotMessage {
    public void send(IMessageClient msgClient, ITextChannel channel);

    public class String implements BotMessage {
        final java.lang.String msgString;

        public String(java.lang.String msgString) {
            this.msgString = msgString;
        }

        @Override
        public void send(IMessageClient msgClient, ITextChannel channel) {
            msgClient.sendString(channel, this);
        }
    }

    public class UserMiniTimes implements BotMessage {
        final IUser requestor;
        final Map<LocalDate, Integer> timesMap;

        public UserMiniTimes(IUser requestor, Map<LocalDate, Integer> timesMap) {
            this.requestor = requestor;
            this.timesMap = timesMap;
        }

        @Override
        public void send(IMessageClient msgClient, ITextChannel channel) {
            msgClient.sendMiniScores(channel, this);
        }
    }

    public class UserMiniTimesStats extends UserMiniTimes {
        public UserMiniTimesStats(IUser requestor, Map<LocalDate, Integer> timesMap) {
            super(requestor, timesMap);
        }

        @Override
        public void send(IMessageClient msgClient, ITextChannel channel) {
            msgClient.sendMiniStats(channel, this);
        }
    }

    public class AcknowledgeMessage implements BotMessage {
        final IMessage message;

        public AcknowledgeMessage(IMessage message) {
            this.message = message;
        }

        @Override
        public void send(IMessageClient msgClient, ITextChannel channel) {
            msgClient.acknowledgeMessage(channel, this);
        }
    }
}