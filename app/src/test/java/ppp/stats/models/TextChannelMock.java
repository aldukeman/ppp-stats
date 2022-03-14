package ppp.stats.models;

public class TextChannelMock implements ITextChannel {
    public String name;
    public long id;
    public Type type;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Type getType() {
        return this.type;
    }
}
