package ppp.stats.models;

import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;

public class DiscordTextChannel implements ITextChannel {
    private final MessageChannel channel;

    public DiscordTextChannel(MessageChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getName() {
        if(this.channel instanceof TextChannel) {
            return ((TextChannel)this.channel).getName();
        }
        return null;
    }

    @Override
    public long getId() {
        return this.channel.getId().asLong();
    }
}
