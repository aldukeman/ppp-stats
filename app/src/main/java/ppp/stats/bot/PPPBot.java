package ppp.stats.bot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ppp.stats.action.IAction;
import ppp.stats.data.IChannelDataManager;
import ppp.stats.logging.ILogger;
import ppp.stats.messenger.IMessageClient;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel;
import ppp.stats.parser.IParser;
import ppp.stats.task.ITask;

abstract public class PPPBot {
    final protected ILogger logger;
    final private List<IParser> parsers;
    final private List<ITask> tasks;

    protected IMessageClient msgClient;
    final private IChannelDataManager dataManager;
    protected ITextChannel channel;
    final private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1);

    public interface MessageProcessor {
        public void process(IMessage message);
    }

    protected PPPBot(ILogger logger, List<IParser> parsers, List<ITask> tasks, IChannelDataManager dataManager) {
        this.logger = logger;
        this.parsers = parsers;
        this.tasks = tasks;
        this.dataManager = dataManager;
    }

    abstract public void login();
    
    public void scheduleTasks() {
        for (ITask task : this.tasks) {
            this.scheduleNextExecution(task);
        }
    }

    abstract public void startListening(MessageProcessor processor);

    private void scheduleNextExecution(ITask task) {
        LocalDateTime next = task.nextExecutionDateTime();
        Duration delay = Duration.between(LocalDateTime.now(), next);
        long delayInNanos = delay.toNanos();
        PPPBot bot = this;
        this.scheduledService.schedule(new Runnable() {
            public void run() {
                List<IBotMessage> messages = task.execute(bot.dataManager);
                for (IBotMessage msg : messages) {
                    msg.send(bot.msgClient, bot.channel);
                }
                bot.scheduleNextExecution(task);
            }
        }, delayInNanos, TimeUnit.NANOSECONDS);

        this.logger.trace("Scheduled " + task + " to execute in " + delay.toSeconds() + " seconds.");
    }

    public boolean processMessage(IMessage msg) {
        this.logger.trace("Received message: " + msg.getContent());

            for (IParser parser : this.parsers) {
                if (parser.supportedChannelTypes().contains(msg.getChannel().getType())) {
                    IAction action = parser.parse(msg);
                    if (action != null) {
                        action.process(msg, this.dataManager)
                                .send(this.msgClient, msg.getChannel());
                        return true;
                    }
                }
            }
            
        return false;
    }
}
