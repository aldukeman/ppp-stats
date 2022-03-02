package ppp.stats.parser;

import org.junit.Test;

import ppp.stats.action.SendBasicMessageAction;
import ppp.stats.action.SendMiniStatsAction;
import ppp.stats.action.SendMiniTimesAction;
import ppp.stats.models.MessageMock;
import ppp.stats.parser.command.ICommand;
import ppp.stats.parser.command.StatsCommand;
import ppp.stats.parser.command.TimesCommand;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class CommandParserTest {
    @Test public void testCommandParser() {
        final Map<String, ICommand> commands = new HashMap<>();
        commands.put("times", new TimesCommand());
        commands.put("stats", new StatsCommand());
        final CommandParser commandParser = new CommandParser(commands);

        final Map<String, String> tests = new HashMap<>();
        tests.put("!ppp stats", SendMiniStatsAction.class.getCanonicalName());
        tests.put("!ppp times", SendMiniTimesAction.class.getCanonicalName());
        tests.put("!ppp asdf", SendBasicMessageAction.class.getCanonicalName());

        MessageMock msgMock = new MessageMock();
        for(Map.Entry<String, String> entry: tests.entrySet()) {
            msgMock.content = entry.getKey();
            assertEquals(commandParser.parse(msgMock).getClass().getCanonicalName(), entry.getValue());
        }
    }
}
