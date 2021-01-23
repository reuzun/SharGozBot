package ceng.estu;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;


import java.sql.SQLOutput;
import java.util.*;

/**
 * @author reuzun
 */
public class DMusic {

    private static final Map<String, Command> commands = new HashMap<>();
    private static String SYSTEM_PREFIX_PROPERTY = "!";
static boolean bool = true;
    static {
        commands.put("ping", event -> event.getMessage()
                .getChannel().block()
                .createMessage("Pong!").block());
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

        commands.put("join", event -> {
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                final VoiceState voiceState = member.getVoiceState().block();
                if (voiceState != null) {
                    final VoiceChannel channel = voiceState.getChannel().block();
                    if (channel != null) {
                        // join returns a VoiceConnection which would be required if we were
                        // adding disconnection features, but for now we are just ignoring it.
                        channel.join(spec -> spec.setProvider(provider)).block();
                    }
                }
            }
        });

        final TrackScheduler scheduler = new TrackScheduler(player);
        commands.put("play", event -> {
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                final VoiceState voiceState = member.getVoiceState().block();
                if (voiceState != null) {
                    final VoiceChannel channel = voiceState.getChannel().block();
                    if (channel != null) {
                        // join returns a VoiceConnection which would be required if we were
                        // adding disconnection features, but for now we are just ignoring it.
                        channel.join(spec -> spec.setProvider(provider)).block();
                    }
                }
            }

            final String content = event.getMessage().getContent();
            final List<String> command = Arrays.asList(content.split(" "));
            if (isLink(command.get(1)) && command.size() == 2) {
                playerManager.loadItem(command.get(1), scheduler);
            } else {
                playerManager.loadItem("ytsearch: " + content.replace(SYSTEM_PREFIX_PROPERTY + "play", ""), scheduler);
            }
            try {
                Thread.sleep(1600);
            } catch (Exception e) {
            }

            if (!bool) {
                try {
                    event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + TrackScheduler.audioPlayStack.peek().getInfo().title +
                            "\nAuthor of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().author +
                            "\nDuration of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().length + "ms" +
                            "\nLink of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().uri).block();
                }catch (EmptyStackException exe){
                    event.getMessage().getChannel().block().createMessage(":\nWe believe that number 2 " +
                            "is an unlucky number. So we do" +
                            " not provide information about that" +
                            "song.").block();
                }
            }
            else{
                event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + TrackScheduler.player.getPlayingTrack().getInfo().title +
                        "\nAuthor of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().author +
                        "\nDuration of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().length + "ms" +
                        "\nLink of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().uri).block();
                bool = false;
            }

        });
        commands.put("pause", event -> {
            player.setPaused(true);
            event.getMessage().getChannel().block().createMessage("Paused...").block();
        });
        commands.put("stop", event -> {
            event.getMessage().getChannel().block().createMessage("Paused...").block();
        });
        commands.put("skip", event -> {
            if(!TrackScheduler.audioPlayStack.isEmpty()) {
                TrackScheduler.player.playTrack(TrackScheduler.audioPlayStack.pop());
                event.getMessage().getChannel().block().createMessage("Next song will be playing ASAP.").block();
            }else
                event.getMessage().getChannel().block().createMessage("No Song is available.").block();
        });
        commands.put("cont", event -> {
            player.setPaused(false);
            event.getMessage().getChannel().block().createMessage("Playing...").block();
        });
        commands.put("setvol", event -> {
            player.setVolume(Integer.parseInt(event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" "+1))));
            event.getMessage().getChannel().block().createMessage("Volume is set to : " + player.getVolume()).block();
        });
        commands.put("mov", event -> {
           player.getPlayingTrack().setPosition(Integer.parseInt(event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" "+1))));
            event.getMessage().getChannel().block().createMessage("Seeked to : " + player.getPlayingTrack().getPosition()).block();
        });
        commands.put("li", event -> {
            int i = 0;
            if(!((Stack<AudioTrack>)TrackScheduler.audioPlayStack.clone()).isEmpty())
            for (AudioTrack t : (Stack<AudioTrack>)TrackScheduler.audioPlayStack.clone())
                event.getMessage().getChannel().block().createMessage("\n" + (i++) + ". song : " + t.getInfo().title + " Duration : " + t.getInfo().length + "ms").block();
            else
                event.getMessage().getChannel().block().createMessage("No song Left." + player.getVolume()).block();
        });


        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
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
                        }
                    }
                });


        client.onDisconnect().block();
    }

    private static boolean isLink(String str) {
        return str.substring(0,4).equals("http");
    }

}
