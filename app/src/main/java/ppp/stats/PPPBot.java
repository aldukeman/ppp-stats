package ppp.stats;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

public class PPPBot {
    private DiscordClient client;

    public PPPBot(String token) {
        this.client = DiscordClient.create(token);
    }

    public static void main(String[] args) {
        final String token = args[0];
        final PPPBot bot = new PPPBot(token);
    
        bot.client.login().flatMapMany(gateway -> gateway.on(MessageCreateEvent.class))
          .map(MessageCreateEvent::getMessage)
          .filter(message -> "!ping".equals(message.getContent()))
          .flatMap(Message::getChannel)
          .flatMap(channel -> channel.createMessage("Pong!"))
          .blockLast();
    }
}
