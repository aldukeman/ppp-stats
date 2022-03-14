package ppp.stats.models;

public class MessageMock implements IMessage {
    public long id = 0;
    public String content = "";
    public ITextChannel channel = null;
    public IUser author = null;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public ITextChannel getChannel() {
        return this.channel;
    }

    @Override
    public IUser getAuthor() {
        return this.author;
    }
}
