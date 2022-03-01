package ppp.stats.models;

public interface IMessage {
    long getId();
    String getContent();
    ITextChannel getChannel();
    IUser getAuthor();
}
