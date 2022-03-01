package ppp.stats.models;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

public class DiscordMessage implements IMessage {
    private Message message;

    public DiscordMessage(Message message) {
        this.message = message;
    }

    public long getId() {
        return message.getId().asLong();
    }

    public String getContent() {
        return this.message.getContent();
    }

    public ITextChannel getChannel() {
        return new DiscordTextChannel((TextChannel)this.message.getChannel().block());
    }

    public IUser getAuthor() {
        return new DiscordUser(this.message.getAuthor().get());
    }
}
