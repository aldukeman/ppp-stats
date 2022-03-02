package ppp.stats.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ppp.stats.action.IAction;
import ppp.stats.action.SendBasicMessageAction;
import ppp.stats.models.IMessage;
import ppp.stats.parser.command.ICommand;

public class CommandParser implements IParser {
    private final Map<String, ICommand> commands;

    public CommandParser(Map<String, ICommand> commands) {
        this.commands = commands;
    }

    private IAction sendHelpAction() {
        String resp = "```";
        resp += "Command not recognized. The following are supported:";
        for(String verb: this.commands.keySet()) {
            resp += "\n- " + verb;
        }
        resp += "\n```";

        return new SendBasicMessageAction(resp);
    }

    @Override
    public IAction parse(IMessage message) {
        List<String> commandElements = Arrays.asList(message.getContent().split(" "));
        if(commandElements.size() == 0) { return null; }
        if(!commandElements.get(0).equals("!ppp")) { return null; }
        if(commandElements.size() == 1) { return this.sendHelpAction(); }
        
        String cmd = commandElements.get(1);
        for(Map.Entry<String, ICommand> entry: this.commands.entrySet()) {
            if(entry.getKey().equals(cmd)) {
                List<String> tokens;
                if(commandElements.size() > 2) {
                    tokens = commandElements.subList(2, commandElements.size());
                } else {
                    tokens = new ArrayList<>();
                }
                return entry.getValue().parse(tokens);
            }
        }

        return this.sendHelpAction();
    }
}
