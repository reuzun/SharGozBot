package ceng.estu.utilities;

import discord4j.core.event.domain.message.MessageCreateEvent;

/**
 * @author reuzun
 */
public interface Command {
        void execute(MessageCreateEvent event);
}