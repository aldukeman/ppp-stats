package ppp.stats.parser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ppp.stats.action.IAction;
import ppp.stats.action.ProcessMiniTimeAction;
import ppp.stats.models.IMessage;
import ppp.stats.models.ITextChannel.Type;

public class MiniCrosswordTimeParser implements IParser {
    private static Pattern miniTimeReport = Pattern.compile("[0-9]?[0-9]:[0-5][0-9] (?i)mini");
    private static Pattern miniTime = Pattern.compile("[0-9]?[0-9]:[0-5][0-9]");

    private Integer getTime(String msg) {
        if(MiniCrosswordTimeParser.miniTimeReport.matcher(msg).matches()) {
            Matcher matcher = MiniCrosswordTimeParser.miniTime.matcher(msg);
            matcher.find();
            String timeString = matcher.group();

            String[] times = timeString.split(":");
            Integer minutes = Integer.parseInt(times[0]);
            Integer seconds = Integer.parseInt(times[1]);
            return minutes.intValue() * 60 + seconds.intValue();
        } else {
            return null;
        }
    }

    @Override
    public IAction parse(IMessage message) {
        Integer time = this.getTime(message.getContent());
        if(time != null) {
            return new ProcessMiniTimeAction(message.getAuthor(), time);
        } else {
            return null;
        }
    }

    @Override
    public List<Type> supportedChannelTypes() {
        return List.of(Type.CHANNEL);
    }
}
