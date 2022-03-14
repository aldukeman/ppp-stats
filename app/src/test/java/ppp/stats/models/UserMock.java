package ppp.stats.models;

public class UserMock implements IUser {
    public long id = 0;
    public String username = "";

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
