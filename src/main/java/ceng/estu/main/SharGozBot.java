package ceng.estu.main;

import ceng.estu.filehandler.FileHandler;

import ceng.estu.utilities.Calculator;
import ceng.estu.utilities.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author reuzun
 */
public class SharGozBot {

    protected static final Map<String, Command> commands = new HashMap<>();
    protected static String SYSTEM_PREFIX_PROPERTY = "\"";

    static {
        commands.put("mem", event -> {
            event.getMessage().getChannel().block().createEmbed(spec -> {
                //spec.setColor(Color.of((float) Math.random(), (float) Math.random(), (float) Math.random()))
                spec.setColor(Color.DARK_GRAY)
                        //.setTitle("1:")
                        .setImage("https://user-images.githubusercontent.com/73116832/105819532-5986be00-5fc9-11eb-9dc5-9a784d2f7147.png");
            }).block();
        });
        commands.put("ping", event -> event.getMessage()
                .getChannel().block()
                .createMessage(
                        "Latency: " +
                                event.getMessage().getClient().getGatewayClient(0).get().getResponseTime().toMillis() +
                                "ms")
                .block());
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


        CommandHandler.initializeCommands(playerManager); //Adjusts the commands from a class in the same package

        EventDispatcher customDispatcher = EventDispatcher.builder()
                .eventScheduler(Schedulers.boundedElastic())
                .build();

        GatewayDiscordClient client = null;

        if(args.length == 1){
            client = DiscordClientBuilder.create(args[0]).build()
                    .gateway().setEventDispatcher(customDispatcher)
                    .setInitialStatus(it -> Presence.doNotDisturb(Activity.playing("type \"help to get commands")))
                    .login()
                    .block();
        }else{
            System.out.println("There is an error about program parameters.");
            System.exit(-1);
        }
        FileHandler.handleMap();

        //client.updatePresence(Presence.online(Activity.playing("something"))).block();

        //client.updatePresence(Presence.doNotDisturb()).block(); //offline bot


        System.out.println("********************************************");
        Mono<Long> guildCount = client.getGuilds().count();
        System.out.println("Bot is available in "+guildCount.block() + " server."); //Bot is online(server count)


        int totalMemberCount = 0;
        List<Guild> guildList = client.getGuilds().collectList().block();
        for(Guild g : guildList){
            totalMemberCount += g.getMembers().count().block();
        }
        System.out.println("Bot is serving to "+ totalMemberCount+ " people.");
        System.out.println("********************************************");


        client.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    final String content = event.getMessage().getContent(); // 3.1 Message.getContent() is a String
                    event:
                    {
                        for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                            // We will be using ! as our "prefix" to any command in the system.
                            if (content.startsWith(SYSTEM_PREFIX_PROPERTY + entry.getKey())) {
                                try {
                                    entry.getValue().execute(event);
                                } catch (FileNotFoundException e) {
                                    //e.printStackTrace();
                                    System.out.println("An error has occurred.");
                                } catch (Exception e){
                                    System.out.println("An error has occurred.");
                                    e.printStackTrace();
                                }
                                break;
                            } else if (content.contains("${")) {

                                //String str = content.substring(content.lastIndexOf("$") + 2, content.lastIndexOf("}")); //1444/12
                                String strToParse = content;

                                String regex = "\\$\\{.*?}";

                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(strToParse);

                                StringBuilder sb = new StringBuilder();
                                sb.append(":\n");
                                int holder = 0;
                                while (matcher.find()) {
                                    String str = strToParse.substring(matcher.start(), matcher.end()).replace("${", "").replace("}", "");
                                    //event.getMessage().getChannel().block().createMessage(":\n"+ (holder++) + ". value : " + String.valueOf(eval(str))).block();
                                    try {
                                        sb.append((holder++) + ". value : " + Calculator.eval(str) + "\n");
                                    } catch (Exception e) {
                                        //if the message includes ${PARAM} but param is invalid. Or situations like not math but that syntax usage.
                                        break event;
                                    }
                                }
                                event.getMessage().getChannel().block().createMessage(sb.toString()).block();
                                break;
                            }
                        }
                    }
                });

        client.onDisconnect().block();

    }
}
