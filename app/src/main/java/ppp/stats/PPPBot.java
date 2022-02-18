package ppp.stats;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import ppp.stats.data.InMemoryDataManager;
import ppp.stats.parser.MiniCrosswordTimeParser;

public class PPPBot {
    private DiscordClient client;
    private GatewayDiscordClient gateway;
    private InMemoryDataManager dataManager;

    public PPPBot(String token) {
        this.client = DiscordClient.create(token);
        this.dataManager = new InMemoryDataManager();
    }

    public void login() {
        this.gateway = this.client.login().block();
    }

    public void startListening() {
        this.gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            this.processMessage(message);
          });
        this.gateway.onDisconnect().block();
    }

    private void processMessage(Message msg) {
        System.out.println("Received message: " + msg.getContent());
        if ("!ping".equals(msg.getContent())) {
            final MessageChannel channel = msg.getChannel().block();
            channel.createMessage("Pong!").block();
        } else if(msg.getContent().contains("mini")) {
            MiniCrosswordTimeParser parser = new MiniCrosswordTimeParser();
            Integer time = parser.getTime(msg.getContent());
            if(time != null) {
                System.out.println("Found mini time: " + time.intValue());
            } else {
                System.out.println("Found \"mini\" but it wasn't a time report");
            }
        }
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Missing a token");
            return;
        }

        final String token = args[0];
        final PPPBot bot = new PPPBot(token);
        bot.login();
        bot.startListening();
    }
}
