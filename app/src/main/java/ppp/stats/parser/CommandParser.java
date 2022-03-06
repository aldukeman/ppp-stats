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
        if(this.commands.isEmpty()) { return new SendBasicMessageAction("No commands are supported"); }

        List<String> lines = new ArrayList<>(this.commands.size() + 5);
        lines.add("```");
        lines.add("Command not recognized. The following are supported:");

        int cmdLength = this.commands.keySet().stream().map(e -> e.length()).max((a, b) -> a - b).get();
        for(var cmd: this.commands.entrySet()) {
            lines.add("\t" + String.format("%-" + cmdLength + "s", cmd.getKey()) + "\t" + cmd.getValue().helpMessage());
        }
        lines.add("```");

        return new SendBasicMessageAction(String.join("\n", lines));
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
