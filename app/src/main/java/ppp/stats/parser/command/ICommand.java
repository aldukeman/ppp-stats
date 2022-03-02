package ppp.stats.parser.command;

import java.util.List;

import ppp.stats.action.IAction;

public interface ICommand {
    IAction parse(List<String> command);
}
