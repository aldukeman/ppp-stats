package ppp.stats.models;

import discord4j.core.object.entity.channel.TextChannel;

public class DiscordTextChannel implements ITextChannel {
    private TextChannel channel;

    public DiscordTextChannel(TextChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getName() {
        return this.channel.getName();
    }

    @Override
    public long getId() {
        return channel.getId().asLong();
    }
}
