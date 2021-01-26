package ceng.estu.utilities;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.io.FileNotFoundException;

/**
 * @author reuzun
 */
public interface Command {
        void execute(MessageCreateEvent event) throws FileNotFoundException;
}
