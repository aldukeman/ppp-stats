package ppp.stats.parser.command;

import java.util.List;

import ppp.stats.action.IAction;
import ppp.stats.action.SendBasicMessageAction;

// Command for trolling a specific person.
public class TrollCommand implements ICommand 
{
    // Test string for proof-of-concept.
    String trollIan = "Ian is a cotton-headed ninny-muggins!";

    @Override
    public IAction parse(List<String> command) {
        if(command.size() == 0) { return new SendBasicMessageAction(trollIan); }
        return null;
    }

    @Override
    public String helpMessage() {
        return "Sends a message to troll the given person.";
    }
}