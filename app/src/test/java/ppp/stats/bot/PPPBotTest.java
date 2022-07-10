package ppp.stats.bot;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.InMemoryDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.NoOpLogger;
import ppp.stats.parser.IParser;
import ppp.stats.task.ITask;

public class PPPBotTest {
    private class PPPBotMock extends PPPBot {
        private MessageProcessor processor;

        PPPBotMock(ILogger logger, List<IParser> parsers, List<ITask> tasks, IChannelDataManager dataManager) {
            super(logger, parsers, tasks, dataManager);
        }

        @Override
        public void login() {
            
        }

        @Override
        public void startListening(MessageProcessor processor) {
            this.processor = processor;
        }

        public processMockMessage(MessageMock msg) {
            this.processor.process(msg);
        }
    }

    @Test
    public void testMiniTimeInput() {
        PPPBot bot = new PPPBotMock(new NoOpLogger(), new ArrayList<>(), new ArrayList<>(), new InMemoryDataManager());
    }
}
