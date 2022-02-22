package ppp.stats.data.model;

public class UserModel {
    final private long id;
    final private String name;

    public UserModel(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() { return this.id; }
    public String getName() { return this.name; }
}
