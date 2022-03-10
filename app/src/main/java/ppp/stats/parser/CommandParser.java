package ppp.stats.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ppp.stats.action.IAction;
import ppp.stats.action.SendBasicMessageAction;
import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel.Type;
import ppp.stats.parser.command.ICommand;

public class CommandParser implements IParser {
    private final Map<String, ICommand> commands;

    public CommandParser(Map<String, ICommand> commands) {
        this.commands = commands;
    }

    private IAction sendHelpAction() {
        if (this.commands.isEmpty()) {
            return new SendBasicMessageAction("No commands are supported");
        }

        List<String> lines = new ArrayList<>(this.commands.size() + 5);
        lines.add("```");
        lines.add("Command not recognized. The following are supported:");

        int cmdLength = this.commands.keySet().stream().map(e -> e.length()).max((a, b) -> a - b).get();
        for (var cmd : this.commands.entrySet()) {
            lines.add("\t" + String.format("%-" + cmdLength + "s", cmd.getKey()) + "\t" + cmd.getValue().helpMessage());
        }
        lines.add("```");

        return new SendBasicMessageAction(String.join("\n", lines));
    }

    @Override
    public IAction parse(IMessage message) {
        List<String> commandElements = Arrays.asList(message.getContent().split(" "));
        if (commandElements.size() == 0) {
            return null;
        }

        boolean isChannel = message.getChannel().getType() == Type.CHANNEL;
        String verb;
        List<String> options;
        if (isChannel) {
            if (!commandElements.get(0).equals("!ppp")) {
                return null;
            }
            if (commandElements.size() == 1) {
                return this.sendHelpAction();
            }
            verb = commandElements.get(1);

            if (commandElements.size() > 2) {
                options = commandElements.subList(2, commandElements.size());
            } else {
                options = new ArrayList<>();
            }
        } else {
            verb = commandElements.get(0);

            if (commandElements.size() > 1) {
                options = commandElements.subList(1, commandElements.size());
            } else {
                options = new ArrayList<>();
            }
        }

        for (Map.Entry<String, ICommand> entry : this.commands.entrySet()) {
            if (entry.getKey().equals(verb)) {
                return entry.getValue().parse(options);
            }
        }

        return this.sendHelpAction();
    }

    @Override
    public List<Type> supportedChannelTypes() {
        return List.of(Type.CHANNEL, Type.DM, Type.GROUP_DM);
    }
}
