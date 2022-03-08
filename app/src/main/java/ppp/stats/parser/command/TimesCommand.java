package ppp.stats.parser.command;

import java.util.List;

import ppp.stats.action.IAction;
import ppp.stats.action.SendBasicMessageAction;
import ppp.stats.action.SendMiniTimesAction;

public class TimesCommand implements ICommand {
    @Override
    public IAction parse(List<String> command) {
        if (command.size() == 0) {
            return new SendMiniTimesAction(10);
        } else if (command.size() == 1) {
            String option = command.get(0);
            if (option.equals("all")) {
                return new SendMiniTimesAction();
            } else {
                try {
                    Double d = Double.parseDouble(option);
                    if (d.intValue() > 0) {
                        return new SendMiniTimesAction(d.intValue());
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    return this.usageMessage();
                }
            }
        }
        return this.usageMessage();
    }

    private SendBasicMessageAction usageMessage() {
        return new SendBasicMessageAction("```Usage: times [all | <number>]\n\t<number> must be positive```");
    }

    @Override
    public String helpMessage() {
        return "Get all the recorded mini crossword times for the user";
    }
}