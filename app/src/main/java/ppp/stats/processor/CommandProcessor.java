package ppp.stats.processor;

import java.util.Map;

import discord4j.core.object.entity.Message;
import ppp.stats.processor.commands.ICommandHandler;

public class CommandProcessor implements IProcessor {
    private Map<String, ICommandHandler> commands;

    public CommandProcessor(Map<String, ICommandHandler> commands) {
        this.commands = commands;
    }

    @Override
    public boolean process(Message msg) {
        String message = msg.getContent();
        if(!message.startsWith("!ppp")) { return false; }
        if(message.length() <= 5) { return false; }

        String cmd = message.substring(5);
        ICommandHandler handler = this.commands.get(cmd);
        if(handler != null) {
            handler.process(msg);
        } else {
            String resp = "```";
            resp += "Command not recognized. The following are supported:";
            for(String verb: this.commands.keySet()) {
                resp += "\n- " + verb;
            }
            resp += "\n```";
            msg.getChannel().block().createMessage(resp).block();
        }

        return true;
    }

    // private String headers() {
    //     return "|Date|Time|\n|:-:|:-:|";
    // }

    // private String rowString(LocalDate date, Integer time) {
    //     int sec = time.intValue();
    //     int min = sec / 60;
    //     sec = sec % 60;
    //     String timeStr = min + ":" + String.format("%2s", sec).replace(" ", "0");
    //     return "|" + date + "|" + timeStr + "|";
    // }
}
