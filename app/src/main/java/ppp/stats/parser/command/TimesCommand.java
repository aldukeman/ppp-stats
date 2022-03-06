package ppp.stats.parser.command;

import java.util.List;

import ppp.stats.action.IAction;
import ppp.stats.action.SendMiniTimesAction;

public class TimesCommand implements ICommand {
    @Override
    public IAction parse(List<String> command) {
        if(command.size() == 0) { return new SendMiniTimesAction(); }
        return null;
    }

    @Override
    public String helpMessage() {
        return "Get all the recorded mini crossword times for the user";
    }
}