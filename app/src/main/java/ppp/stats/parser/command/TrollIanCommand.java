package ppp.stats.parser.command;

import java.util.List;

import ppp.stats.action.IAction;
import ppp.stats.action.SendBasicMessageAction;

// Command for trolling Ian because funny.
public class TrollIanCommand implements ICommand 
{
    // Test string for proof-of-concept.
    String trollIan = "Ian is a cotton-headed ninny-muggins!";

    @Override
    public IAction parse(List<String> tokens) {
        if(tokens.size() == 0) { return new SendBasicMessageAction(trollIan); }
        return null;
    }

    @Override
    public String helpMessage() {
        return "Sends a message to troll Ian.";
    }
}