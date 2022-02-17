package ppp.stats;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class PPPBot {
    private DiscordClient client;
    private GatewayDiscordClient gateway;

    public PPPBot(String token) {
        this.client = DiscordClient.create(token);
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
