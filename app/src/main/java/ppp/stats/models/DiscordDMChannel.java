package ppp.stats.models;

import discord4j.core.object.entity.channel.PrivateChannel;

public class DiscordDMChannel implements DiscordTextChannel {
    private final PrivateChannel channel;

    public DiscordDMChannel(PrivateChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getName() {
        String name = this.channel.getRecipients().stream().findFirst().get().getUsername();
        return "DM: " + name;
    }

    @Override
    public long getId() {
        return this.channel.getId().asLong();
    }

    @Override
    public Type getType() {
        return this.channel.getRecipients().size() > 2 ? Type.GROUP_DM : Type.DM;
    }
}
