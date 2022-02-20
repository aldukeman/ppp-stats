package ppp.stats.processor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.MessageCreateSpec;
import ppp.stats.data.IDataManager;
import ppp.stats.data.UserTimesDictionary;
import ppp.stats.models.DiscordUser;

public class CommandProcessor implements IProcessor {
    private IDataManager dataManager;

    public CommandProcessor(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean process(Message msg) {
        String message = msg.getContent();
        if(!message.startsWith("!ppp")) { return false; }
        if(message.length() <= 5) { return false; }

        String cmd = message.substring(5);
        if(cmd.equals("times")) {
            User u = msg.getAuthor().get();
            if(u != null) {
                DiscordUser dUser = new DiscordUser(u);
                UserTimesDictionary dict = this.dataManager.getTimesForUserId(dUser.getId());

                String resp;
                if(dict == null || dict.size() == 0) {
                    resp = "No times have been recorded for " + u.getUsername();
                } else {
                    Collection<Integer> times = dict.values();
                    List<String> timeStrings = times.stream().map(t -> t + "").toList();

                    resp = "Found " + timeStrings.size() + " records for " + u.getUsername()  + "\n";

                    List<LocalDate> dates = new ArrayList<LocalDate>(dict.keySet());
                    Collections.sort(dates, (e1, e2) -> { return e1.compareTo(e2); });
                    List<String> rows = dates.stream().map(e -> this.responseString(e, dict.get(e))).toList();
                    resp += String.join("\n", rows);
                }
                msg.getChannel().block().createMessage(resp).block();
            } else {
                msg.getChannel().block().createMessage("Couldn't find user").block();
            }
        } else {
            String resp = cmd + " not recognized\nThe following are supported:\n- times";
            msg.getChannel().block().createMessage(resp);
        }
        
        return true;
    }

    private String responseString(LocalDate date, Integer time) {
        int sec = time.intValue();
        int min = sec / 60;
        sec = sec % 60;
        String timeStr = min + ":" + String.format("%2s", sec).replace(" ", "0");
        return date.toString() + " " + timeStr;
    }
}
