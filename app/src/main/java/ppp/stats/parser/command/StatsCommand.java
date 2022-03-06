package ppp.stats.parser.command;

import java.util.List;

import ppp.stats.action.IAction;
import ppp.stats.action.SendMiniStatsAction;

public class StatsCommand implements ICommand {
    @Override
    public IAction parse(List<String> tokens) {
        if(tokens.size() == 0) { return new SendMiniStatsAction(); }
        return null;
    }

    @Override
    public String helpMessage() {
        return "Produce a variey of statistics about the user's crossword times";
    }
}
