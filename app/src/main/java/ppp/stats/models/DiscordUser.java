package ppp.stats.models;

import discord4j.core.object.entity.User;

public class DiscordUser implements IUser {
    private User user;

    public DiscordUser(User user) {
        this.user = user;
    }

    public long getId() {
        return this.user.getId().asLong();
    }

    public String getUsername() {
        return this.user.getUsername();
    }
}
