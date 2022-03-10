package ppp.stats.models;

import discord4j.core.object.entity.Message;

public class DiscordMessage implements IMessage {
    private final Message message;
    private final DiscordTextChannel channel;

    public DiscordMessage(Message message, DiscordTextChannel channel) {
        this.message = message;
        this.channel = channel;
    }

    public long getId() {
        return message.getId().asLong();
    }

    public String getContent() {
        return this.message.getContent();
    }

    public ITextChannel getChannel() {
        return this.channel;
    }

    public IUser getAuthor() {
        return new DiscordUser(this.message.getAuthor().get());
    }
}
