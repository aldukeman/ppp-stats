package ppp.stats.parser;

import org.junit.Test;

import ppp.stats.action.SendBasicMessageAction;
import ppp.stats.action.SendMiniStatsAction;
import ppp.stats.action.SendMiniTimesAction;
import ppp.stats.models.MessageMock;
import ppp.stats.models.TextChannelMock;
import ppp.stats.models.ITextChannel.Type;
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

        final Map<String, String> channelTests = new HashMap<>();
        channelTests.put("!ppp stats", SendMiniStatsAction.class.getCanonicalName());
        channelTests.put("!ppp times", SendMiniTimesAction.class.getCanonicalName());
        channelTests.put("!ppp times all", SendMiniTimesAction.class.getCanonicalName());
        channelTests.put("!ppp times 12", SendMiniTimesAction.class.getCanonicalName());
        channelTests.put("!ppp times -3", SendBasicMessageAction.class.getCanonicalName());
        channelTests.put("!ppp asdf", SendBasicMessageAction.class.getCanonicalName());

        MessageMock msgMock = new MessageMock();
        TextChannelMock chMock = new TextChannelMock();
        chMock.type = Type.CHANNEL;
        msgMock.channel = chMock;
        
        for(Map.Entry<String, String> entry: channelTests.entrySet()) {
            msgMock.content = entry.getKey();
            assertEquals(commandParser.parse(msgMock).getClass().getCanonicalName(), entry.getValue());
        }

        final Map<String, String> dmTests = new HashMap<>();
        dmTests.put("stats", SendMiniStatsAction.class.getCanonicalName());
        dmTests.put("times", SendMiniTimesAction.class.getCanonicalName());
        dmTests.put("times all", SendMiniTimesAction.class.getCanonicalName());
        dmTests.put("times 12", SendMiniTimesAction.class.getCanonicalName());
        dmTests.put("times -3", SendBasicMessageAction.class.getCanonicalName());
        dmTests.put("asdf", SendBasicMessageAction.class.getCanonicalName());

        chMock.type = Type.DM;
        
        for(Map.Entry<String, String> entry: dmTests.entrySet()) {
            msgMock.content = entry.getKey();
            assertEquals(commandParser.parse(msgMock).getClass().getCanonicalName(), entry.getValue());
        }
    }
}
