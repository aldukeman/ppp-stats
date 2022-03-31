package ppp.stats.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ppp.stats.action.IAction;
import ppp.stats.action.ProcessWordleResultAction;
import ppp.stats.data.model.WordleResultModel;
import ppp.stats.data.model.WordleResultModel.CellType;
import ppp.stats.logging.ILogger;
import ppp.stats.logging.NoOpLogger;
import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel.Type;
import ppp.stats.utility.Pair;

public class WordleResultParser implements IParser {
    private ILogger logger;

    public WordleResultParser() {
        this(NoOpLogger.shared);
    }

    public WordleResultParser(ILogger logger) {
        this.logger = logger;
    }

    private static final Pattern HEADER_PATTERN = Pattern.compile("Wordle (\\d+) (\\d|X)/6(\\*?)");

    private Pair<Integer, WordleResultModel> getModel(String msg) {
        List<String> lines = msg.lines().toList();
        if (lines.size() < 3) {
            return null;
        }

        String header = lines.get(0);
        Matcher matcher = WordleResultParser.HEADER_PATTERN.matcher(header);
        int wordleNum;
        int guesses;
        boolean isHard;
        if (matcher.find()) {
            wordleNum = Integer.parseInt(matcher.group(1));
            String guessesString = matcher.group(2);
            if (guessesString.equals("X")) {
                guesses = 6;
            } else {
                guesses = Integer.parseInt(matcher.group(2));
            }
            if (matcher.groupCount() == 3) {
                isHard = !matcher.group(3).isEmpty();
            } else {
                isHard = false;
            }
        } else {
            return null;
        }

        if (!lines.get(1).isEmpty()) {
            return null;
        }
        if(lines.size() != guesses + 2) {
            return null;
        }

        List<List<WordleResultModel.CellType>> rows = new ArrayList<>();
        for (int i = 0; i < guesses; ++i) {
            List<WordleResultModel.CellType> row = new ArrayList<>();
            String line = lines.get(2 + i);
            byte[] bytes = line.getBytes();
            for (int j = 0; j < bytes.length; ++j) {
                if (bytes[j] == (byte) 0xe2 && bytes[j + 1] == (byte) 0xac) {
                    if (bytes[j + 2] == (byte) 0x9c || bytes[j + 2] == (byte) 0x9b) {
                        row.add(CellType.BAD);
                        j += 2;
                    } else {
                        return null;
                    }
                } else if (bytes[j] == (byte) 0xf0 && bytes[j + 1] == (byte) 0x9f && bytes[j + 2] == (byte) 0x9f) {
                    if (bytes[j + 3] == (byte) 0xa8) {
                        row.add(CellType.WRONG_SPOT);
                    } else if (bytes[j + 3] == (byte) 0xa9) {
                        row.add(CellType.GOOD);
                    } else {
                        return null;
                    }
                    j += 3;
                } else {
                    return null;
                }
            }

            if (row.size() != 5) {
                return null;
            }

            rows.add(row);
        }

        return Pair.of(Integer.valueOf(wordleNum), WordleResultModel.from(rows, isHard));
    }

    @Override
    public IAction parse(IMessage message) {
        Pair<Integer, WordleResultModel> result = this.getModel(message.getContent());
        if (result != null) {
            return new ProcessWordleResultAction(result.first.intValue(), result.second, this.logger);
        } else {
            return null;
        }
    }

    @Override
    public List<Type> supportedChannelTypes() {
        return List.of(Type.CHANNEL);
    }
}