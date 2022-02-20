package ppp.stats.processor.commands;

import discord4j.core.object.entity.Message;

public interface ICommandHandler {
    void process(Message msg);
}
