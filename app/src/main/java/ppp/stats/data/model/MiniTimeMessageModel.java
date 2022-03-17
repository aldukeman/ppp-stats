package ppp.stats.data.model;

public class MiniTimeMessageModel {
    final private Long messageId;
    final private int time;
    final private long userId;

    public MiniTimeMessageModel(Long messageId, int time, long userId) {
        this.messageId = messageId;
        this.time = time;
        this.userId = userId;
    }

    public Long getMessageId() { return this.messageId; }
    public int getTime() { return this.time; }
    public long getUserId() { return this.userId; }
}
