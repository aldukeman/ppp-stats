package ppp.stats.processor;

import java.util.Map;

import ppp.stats.client.BotMessage;
import ppp.stats.client.IMessageClient;
import ppp.stats.logging.ILogger;
import ppp.stats.models.IMessage;
import ppp.stats.processor.commands.ICommandHandler;

public class CommandProcessor implements IProcessor {
    private final Map<String, ICommandHandler> commands;
    private final ILogger logger;

    public CommandProcessor(Map<String, ICommandHandler> commands, ILogger logger) {
        this.commands = commands;
        this.logger = logger;
    }

    @Override
    public boolean process(IMessage msg, IMessageClient msgClient) {
        String message = msg.getContent();
        if(!message.startsWith("!ppp")) { return false; }
        if(message.length() <= 5) { return false; }

        String cmd = message.substring(5);
        ICommandHandler handler = this.commands.get(cmd);
        if(handler != null) {
            this.logger.trace("Handling command: " + cmd);
            handler.process(msg, msgClient);
        } else {
            String resp = "```";
            resp += "Command not recognized. The following are supported:";
            for(String verb: this.commands.keySet()) {
                resp += "\n- " + verb;
            }
            resp += "\n```";

            msgClient.sendString(msg.getChannel(), new BotMessage.String(resp));
        }

        return true;
    }
}
