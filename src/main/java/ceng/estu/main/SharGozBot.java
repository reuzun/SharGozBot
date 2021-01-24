package ceng.estu.main;

import ceng.estu.utilities.Command;
import ceng.estu.utilities.LavaPlayerAudioProvider;
import ceng.estu.utilities.TrackScheduler;
import ceng.estu.webhandle.WebHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import reactor.core.scheduler.Schedulers;


import java.time.Instant;
import java.util.*;

/**
 * @author reuzun
 */
public class SharGozBot {

    protected static final Map<String, Command> commands = new HashMap<>();
    protected static String SYSTEM_PREFIX_PROPERTY = "!";

    static {
        commands.put("ping", event -> event.getMessage()
                .getChannel().block()
                .createMessage("Your ping is calculated as 49ms.").block());
        commands.put("deniz senin kardeşin", event -> event.getMessage()
                .getChannel().block()
                .createMessage("baba yalan söylüyorsun bu olamaz nayır.").block());
    }

    public static void main(String[] args) {

        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        // This is an optimization strategy that Discord4J can utilize. It is not important to understand
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);
        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();
        // We will be creating LavaPlayerAudioProvider in the next step
        AudioProvider provider = new LavaPlayerAudioProvider(player);

        CommandHandler.initializeCommands(provider, playerManager, player); //Adjusts the commands from a class in the same package

        EventDispatcher customDispatcher = EventDispatcher.builder()
                .eventScheduler(Schedulers.boundedElastic())
                .build();

        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
                .gateway().setEventDispatcher(customDispatcher)
                .login()
                .block();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    final String content = event.getMessage().getContent(); // 3.1 Message.getContent() is a String
                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        // We will be using ! as our "prefix" to any command in the system.
                        if (content.startsWith(SYSTEM_PREFIX_PROPERTY + entry.getKey())) {
                            entry.getValue().execute(event);
                            break;
                        } else if (content.contains("${")) {
                            String str = content.substring(content.lastIndexOf("$") + 2, content.lastIndexOf("}")); //1444/12
                            /*
                            String process =
                            String regexRight = "[^0-9].*";
                            String regexLeft = ".*[^0-9]";

                            Pattern patternleft = Pattern.compile(regexRight);
                            Matcher matcherleft = patternleft.matcher(str);

                            Pattern patternright = Pattern.compile(regexRight);
                            Matcher matcherright = patternright.matcher(str);
                            */

                            //basic idea. can be improved.
                            if (str.contains("*")) {
                                double res = Double.parseDouble(str.substring(0, str.lastIndexOf("*"))) * Double.parseDouble(str.substring(str.lastIndexOf("*") + 1, str.length()));
                                event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                break;
                            } else if (str.contains("/")) {
                                double res = Double.parseDouble(str.substring(0, str.lastIndexOf("/"))) / Double.parseDouble(str.substring(str.lastIndexOf("/") + 1, str.length()));
                                event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                break;
                            } else if (str.contains("+")) {
                                double res = Double.parseDouble(str.substring(0, str.lastIndexOf("+"))) + Double.parseDouble(str.substring(str.lastIndexOf("+") + 1, str.length()));
                                event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                break;
                            } else if (str.contains("-")) {
                                double res = Double.parseDouble(str.substring(0, str.lastIndexOf("-"))) - Double.parseDouble(str.substring(str.lastIndexOf("-") + 1, str.length()));
                                event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                break;
                            } else if (str.contains("^")) {
                                double res = Math.pow(Double.parseDouble(str.substring(0, str.lastIndexOf("^"))), Double.parseDouble(str.substring(str.lastIndexOf("^") + 1, str.length())));
                                event.getMessage().getChannel().block().createMessage(String.valueOf(res)).block();
                                break;
                            }
                        }
                    }
                });


        client.onDisconnect().block();
    }



}
