package ppp.stats.models;

import discord4j.core.object.entity.channel.TextChannel;

public class DiscordGuildChannel implements DiscordTextChannel {
    private final TextChannel channel;

    public DiscordGuildChannel(TextChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getName() {
        return this.channel.getName();
    }

    @Override
    public long getId() {
        return this.channel.getId().asLong();
    }

    @Override
    public Type getType() {
        return Type.CHANNEL;
    }
}
