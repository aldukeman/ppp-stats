package ppp.stats.processor.commands;

import java.time.LocalDate;
import java.util.List;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;

import ppp.stats.data.IDataManager;
import ppp.stats.data.UserTimesDictionary;
import ppp.stats.models.DiscordUser;

public class TimesCommandHandler implements ICommandHandler {
    private IDataManager dataManager;

    public TimesCommandHandler(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void process(Message msg) {
        User u = msg.getAuthor().get();
        if (u != null) {
            DiscordUser dUser = new DiscordUser(u);
            UserTimesDictionary dict = this.dataManager.getTimesForUserId(dUser.getId());

            String resp;
            if (dict == null || dict.size() == 0) {
                resp = "No times have been recorded for " + u.getUsername();
                msg.getChannel().block().createMessage(resp).block();
            } else {
                List<LocalDate> dates = dict.keySet().stream().sorted((e1, e2) -> e1.compareTo(e2)).toList();
                List<String> dateStrings = dates.stream().map(d -> d.toString()).toList();
                String dateString = String.join("\n", dateStrings);

                List<Integer> times = dates.stream().map(d -> dict.get(d)).toList();
                List<String> timeStrings = times.stream().map(t -> this.timeString(t)).toList();
                String timeString = String.join("\n", timeStrings);

                EmbedCreateSpec spec = EmbedCreateSpec.builder()
                        .addField("Date", dateString, true)
                        .addField("Time", timeString, true)
                        .build();

                msg.getChannel().block().createMessage(spec).block();

                // List<LocalDate> dates = new ArrayList<LocalDate>(dict.keySet());
                // Collections.sort(dates, (e1, e2) -> {
                // return e1.compareTo(e2);
                // });
                // List<String> rows = dates.stream().map(d -> this.rowString(d,
                // dict.get(d))).toList();
                // resp += String.join("\n", rows);
            }
        } else {
            msg.getChannel().block().createMessage("Couldn't find user").block();
        }
    }

    private String timeString(Integer sec) {
        int seconds = sec.intValue();
        int min = seconds / 60;
        seconds = seconds % 60;
        return min + ":" + String.format("%2s", seconds).replace(" ", "0");
    }
}
