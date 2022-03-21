package ppp.stats.action;

import ppp.stats.data.IChannelDataManager;
import ppp.stats.data.model.WordleResultModel;
import ppp.stats.logging.ILogger;
import ppp.stats.messenger.message.IBotMessage;
import ppp.stats.messenger.message.ReactToMessage;
import ppp.stats.models.IMessage;
import ppp.stats.models.IUser;

public class ProcessWordleResultAction implements IAction {
    public final int puzzleNum;
    public final WordleResultModel result;
    private final ILogger logger;

    public ProcessWordleResultAction(int puzzleNum, WordleResultModel result, ILogger logger) {
        this.puzzleNum = puzzleNum;
        this.result = result;
        this.logger = logger;
    }
    
    @Override
    public IBotMessage process(IMessage message, IChannelDataManager dataManager) {
        IUser author = message.getAuthor();
        this.logger.trace("Adding wordle result for user " + author.getId());
        dataManager.setUserName(author.getId(), author.getUsername());
        dataManager.addWordleResult(author.getId(), IChannelDataManager.WordleDate(), this.result, message.getId());
        return new ReactToMessage(message.getId(), "🤖");
    }
}
