package ppp.stats.models;

import discord4j.core.object.entity.User;

public class DiscordUser {
    private User user;

    public DiscordUser(User user) {
        this.user = user;
    }

    public long getId() {
        return this.user.getId().asLong();
    }
}
