package ppp.stats.processor;

import discord4j.core.object.entity.Message;

public interface IProcessor {
    boolean process(Message msg);
}
