package ppp.stats.parser;

import org.junit.Test;

import ppp.stats.action.ProcessWordleResultAction;
import ppp.stats.models.MessageMock;

import static org.junit.Assert.*;

import java.util.Set;

public class WordleResultParserTest {
    @Test
    public void testWordleResultParser() {
        WordleResultParser parser = new WordleResultParser();

        Set<String> stringPositives = Set.of(
                "Wordle 274 4/6*\n\n🟨⬜⬜⬜🟨\n🟨⬜⬜🟩🟨\n⬜🟩🟩🟩⬜\n🟩🟩🟩🟩🟩",
                "Wordle 274 3/6*\n\n⬛⬛⬛🟩🟨\n⬛⬛🟩🟩🟨\n🟩🟩🟩🟩🟩",
                "Wordle 274 3/6\n\n⬛⬛⬛🟩🟨\n⬛⬛🟩🟩🟨\n🟩🟩🟩🟩🟩",
                "Wordle 282 X/6*\n\n⬜🟩⬜🟩⬜\n⬜🟩🟩🟩🟩\n⬜🟩🟩🟩🟩\n⬜🟩🟩🟩🟩\n⬜🟩🟩🟩🟩\n⬜🟩🟩🟩🟩");

        MessageMock mock = new MessageMock();
        for (var e : stringPositives) {            
            mock.content = e;
            assertTrue(parser.parse(mock) instanceof ProcessWordleResultAction);
        }

        Set<String> stringNegatives = Set.of(
                "4 mini",
                "0:68 mini",
                "my mini time was 4:20",
                "Wordle 282 5/6*\n\n⬜🟩⬜🟩⬜\n⬜🟩🟩🟩🟩\n⬜🟩🟩🟩🟩\n⬜🟩🟩🟩🟩\n⬜🟩🟩🟩🟩\n⬜🟩🟩🟩🟩"); // invalid guess count
        for (String s : stringNegatives) {
            mock.content = s;
            assertNull(parser.parse(mock));
        }
    }
}
