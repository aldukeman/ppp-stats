package ppp.stats.bot;

import ppp.stats.models.IMessage;

public interface IBot {
    public interface MessageProcessor {
        public void process(IMessage message);
    }

    void login();
    void startListening(MessageProcessor processor);
}
