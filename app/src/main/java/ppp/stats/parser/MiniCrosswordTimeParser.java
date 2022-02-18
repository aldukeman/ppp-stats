package ppp.stats.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniCrosswordTimeParser {
    private static Pattern miniTimeReport = Pattern.compile("[0-9]?[0-9]:[0-9][0-9] mini");
    private static Pattern miniTime = Pattern.compile("[0-9]?[0-9]:[0-9][0-9]");

    public Integer getTime(String msg) {
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
}
