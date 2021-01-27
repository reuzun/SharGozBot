package ceng.estu.main;

import ceng.estu.filehandler.FileHandler;
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
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Color;
import discord4j.voice.AudioProvider;
import org.apache.bcel.classfile.ExceptionTable;


import static ceng.estu.main.SharGozBot.commands;
import static ceng.estu.main.SharGozBot.SYSTEM_PREFIX_PROPERTY;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author reuzun
 */
class CommandHandler {

    //static String musicRoomId = null;
    static boolean isPlaylist = false;

    public  static Stack<AudioTrack> audioPlayStack;
    public  static boolean isLooped;
    public static AudioPlayer player;
    public static TrackScheduler scheduler;

    protected static void initializeCommands(AudioPlayerManager playerManager) {




        commands.put("join", event -> {
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                final VoiceState voiceState = member.getVoiceState().block();
                if (voiceState != null) {
                    final VoiceChannel channel = voiceState.getChannel().block();
                    if (channel != null) {
                        // join returns a VoiceConnection which would be required if we were
                        // adding disconnection features, but for now we are just ignoring it.
                        String guildId = event.getGuildId().get().asString();
                        channel.join(spec -> spec.setProvider(getProvider(guildId))).block();
                    }
                }
            }

        }

        );


        commands.put("play", event -> {
            try {
                setup(event.getGuildId().get().asString());
            }catch (Exception e){
                //did not join before play
                final Member member = event.getMember().orElse(null);
                if (member != null) {
                    final VoiceState voiceState = member.getVoiceState().block();
                    if (voiceState != null) {
                        final VoiceChannel channel = voiceState.getChannel().block();
                        if (channel != null) {
                            // join returns a VoiceConnection which would be required if we were
                            // adding disconnection features, but for now we are just ignoring it.
                            String guildId = event.getGuildId().get().asString();
                            channel.join(spec -> spec.setProvider(getProvider(guildId))).block();
                        }
                    }
                }
                setup(event.getGuildId().get().asString());
            }

            final String content = event.getMessage().getContent();
            final List<String> command = Arrays.asList(content.replace(SYSTEM_PREFIX_PROPERTY + "play", "").replace(" ", ""));

            if (command.get(0).length() > 4 && isLink(command.get(0))) {
                isPlaylist = true;
                System.out.println(command.get(0).replace(" ", ""));
                playerManager.loadItem(command.get(0).replace(" ", ""), scheduler);
            } else {
                isPlaylist = false;
                playerManager.loadItem("ytsearch: " + content.replace(SYSTEM_PREFIX_PROPERTY + "play", "").replaceFirst(" ", ""), scheduler);
            }
            //First song
            try {
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                }

                String str = FileHandler.map.get(event.getGuildId().get().asString());

                if (str == null) {
                    if (audioPlayStack.isEmpty() && player.getPlayingTrack() != null) {

                        if (!player.getPlayingTrack().getInfo().isStream)
                            event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + player.getPlayingTrack().getInfo().title +
                                    "\nAuthor of Song : " + player.getPlayingTrack().getInfo().author +
                                    "\nDuration of Song : " + player.getPlayingTrack().getInfo().length + "ms" +
                                    "\nLink of Song : " + player.getPlayingTrack().getInfo().uri).block();
                        else
                            event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + player.getPlayingTrack().getInfo().title +
                                    "\nAuthor of Song : " + player.getPlayingTrack().getInfo().author +
                                    "\nDuration of Song : " + "Live Stream" +
                                    "\nLink of Song : " + player.getPlayingTrack().getInfo().uri).block();
                    } else { //non-first songs.
                        if (!audioPlayStack.peek().getInfo().isStream)
                            event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + audioPlayStack.peek().getInfo().title +
                                    "\nAuthor of Song : " + audioPlayStack.peek().getInfo().author +
                                    "\nDuration of Song : " + audioPlayStack.peek().getInfo().length + "ms" +
                                    "\nLink of Song : " + audioPlayStack.peek().getInfo().uri).block();
                        else
                            event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + audioPlayStack.peek().getInfo().title +
                                    "\nAuthor of Song : " + audioPlayStack.peek().getInfo().author +
                                    "\nDuration of Song : " + "Live Stream" +
                                    "\nLink of Song : " + audioPlayStack.peek().getInfo().uri).block();
                    }
                } else {
                    if (audioPlayStack.isEmpty() && player.getPlayingTrack() != null) {

                        if (!player.getPlayingTrack().getInfo().isStream)
                            event.getClient().getChannelById(Snowflake.of(str)).block().getRestChannel().createMessage(":\nPlaying song is : " + player.getPlayingTrack().getInfo().title +
                                    "\nAuthor of Song : " + player.getPlayingTrack().getInfo().author +
                                    "\nDuration of Song : " + player.getPlayingTrack().getInfo().length + "ms" +
                                    "\nLink of Song : " + player.getPlayingTrack().getInfo().uri).block();
                        else
                            event.getClient().getChannelById(Snowflake.of(str)).block().getRestChannel().createMessage(":\nPlaying song is : " + player.getPlayingTrack().getInfo().title +
                                    "\nAuthor of Song : " + player.getPlayingTrack().getInfo().author +
                                    "\nDuration of Song : " + "Live Stream" +
                                    "\nLink of Song : " + player.getPlayingTrack().getInfo().uri).block();
                    } else { //non-first songs.
                        if (!audioPlayStack.peek().getInfo().isStream)
                            event.getClient().getChannelById(Snowflake.of(str)).block().getRestChannel().createMessage(":\nPlaying song is : " + audioPlayStack.peek().getInfo().title +
                                    "\nAuthor of Song : " + audioPlayStack.peek().getInfo().author +
                                    "\nDuration of Song : " + audioPlayStack.peek().getInfo().length + "ms" +
                                    "\nLink of Song : " + audioPlayStack.peek().getInfo().uri).block();
                        else
                            event.getClient().getChannelById(Snowflake.of(str)).block().getRestChannel().createMessage(":\nPlaying song is : " + audioPlayStack.peek().getInfo().title +
                                    "\nAuthor of Song : " + audioPlayStack.peek().getInfo().author +
                                    "\nDuration of Song : " + "Live Stream" +
                                    "\nLink of Song : " + audioPlayStack.peek().getInfo().uri).block();
                    }

                }
            } catch (Exception e) {
                System.out.println("e");
            }

        });
        commands.put("pause", event -> {
            setup(event.getGuildId().get().asString());
            player.setPaused(true);
            event.getMessage().getChannel().block().createMessage("Paused...").block();
        });
        commands.put("stop", event -> {
            setup(event.getGuildId().get().asString());
            player.setPaused(true);
            event.getMessage().getChannel().block().createMessage("Paused...").block();
        });
        commands.put("skip", event -> {
            setup(event.getGuildId().get().asString());
            CommandHandler.scheduler.setLooped(false);
            if (!audioPlayStack.isEmpty()) {
                CommandHandler.scheduler.setLastPlayedSong(audioPlayStack.peek());
                player.playTrack(audioPlayStack.pop());
                event.getMessage().getChannel().block().createMessage("Next song will be playing ASAP.").block();
            } else
                event.getMessage().getChannel().block().createMessage("No Song is available.").block();
        });
        commands.put("cont", event -> {
            setup(event.getGuildId().get().asString());
            player.setPaused(false);
            event.getMessage().getChannel().block().createMessage("Playing...").block();
        });
        commands.put("setvol", event -> {
            setup(event.getGuildId().get().asString());
            player.setVolume(Integer.parseInt(event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" ")).replace(" ", "")));
            event.getMessage().getChannel().block().createMessage("Volume is set to : " + player.getVolume()).block();
        });
        commands.put("gvol", event -> {
            setup(event.getGuildId().get().asString());
            event.getMessage().getChannel().block().createMessage(String.valueOf(player.getVolume())).block();
        });
        commands.put("mov", event -> {
            setup(event.getGuildId().get().asString());
            player.getPlayingTrack().setPosition(Integer.parseInt(event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" ")).replace(" ", "")));
            try {
                Thread.sleep(225);
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.getMessage().getChannel().block().createMessage("Seeked to : " + player.getPlayingTrack().getPosition()).block();
        });
        commands.put("li", event -> {
            setup(event.getGuildId().get().asString());
            int i = 0;
            if (!((Stack<AudioTrack>) audioPlayStack.clone()).isEmpty()) {
                StringBuilder sb = new StringBuilder(":");
                for (AudioTrack t : (Stack<AudioTrack>) audioPlayStack.clone()) {
                    if (!t.getInfo().isStream)
                        sb.append("\n" + (i++) + ". song : " + t.getInfo().title + " Duration : " + t.getInfo().length + "ms");
                    else
                        sb.append("\n" + (i++) + ". song : " + t.getInfo().title + " Duration : " + "Live Stream");
                }
                event.getMessage().getChannel().block().createMessage(sb.toString()).block().delete().delaySubscription(Duration.ofMillis(3500)).block();
            } else
                event.getMessage().getChannel().block().createMessage("No song Left." + player.getVolume()).block();
        });
        commands.put("olddel", event -> {
            List<Message> messageList = event.getMessage().getChannel().block().getMessagesBefore(Snowflake.of(Instant.now())).collectList().block();
            System.out.println("Ended");

            System.out.println("Size is : " + messageList.size());
            event.getMessage().getChannel().block().createMessage("There is : " + messageList.size() + " Messages on this channel.").block();
            event.getMessage().getChannel().block().createMessage("First message is :" + messageList.get(messageList.size() - 1).getContent()).block();
            event.getMessage().getChannel().block().createMessage("Last message is : " + messageList.get(0).getContent()).block();

            for (Message m : messageList)
                m.delete().block();
        });
        commands.put("del", event -> {
            try {
                try {
                    String delStr = event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" ")).replace(" ", "");
                    int delCount = Integer.parseInt(delStr);

                    event.getMessage().getChannel().cast(TextChannel.class).subscribe(channel -> {
                        channel.bulkDeleteMessages(channel.getMessagesBefore(event.getMessage().getId()).take(delCount)).subscribe(m -> {
                        });
                    });
                    event.getMessage().getChannel().block().createMessage("Deleted message count : " + delCount).block();
                } catch (Exception e) {
                    event.getMessage().getChannel().cast(TextChannel.class).subscribe(channel -> {
                        channel.bulkDeleteMessages(channel.getMessagesBefore(event.getMessage().getId()).take(50)).subscribe(m -> {
                        });
                    });
                    event.getMessage().getChannel().block().createMessage("Deleted message count : " + 50).block();
                }


            } catch (Exception e) {
                //do nothing
                System.out.println("error");
            }
        });
        commands.put("lk", event -> {
            List<Message> messageList = event.getMessage().getChannel().block().getMessagesBefore(Snowflake.of(Instant.now())).collectList().block();

            System.out.println("Size is : " + messageList.size());
            event.getMessage().getChannel().block().createMessage("There is : " + messageList.size() + " Messages on this channel.").block();
            event.getMessage().getChannel().block().createMessage("First message is :" + messageList.get(messageList.size() - 1).getContent()).block();
            event.getMessage().getChannel().block().createMessage("Last message is : " + messageList.get(0).getContent()).block();
        });
        commands.put("lp", event -> {
            setup(event.getGuildId().get().asString());

            CommandHandler.scheduler.setLooped(!CommandHandler.scheduler.isLooped());
            if (CommandHandler.scheduler.isLooped())
                event.getMessage().getChannel().block().createMessage("Loop enabled").block();
            else
                event.getMessage().getChannel().block().createMessage("Loop disabled").block();


           // System.out.println("loop is disabled for a short time");
          //  event.getMessage().getChannel().block().createMessage("loop is disabled for a short time").block();
        });
        commands.put("fw", event -> {
            setup(event.getGuildId().get().asString());
            player.getPlayingTrack().setPosition(player.getPlayingTrack().getPosition() + 60000);
        });
        commands.put("bw", event -> {
            setup(event.getGuildId().get().asString());
            player.getPlayingTrack().setPosition(player.getPlayingTrack().getPosition() - 60000);
        });
        commands.put("getvol", event -> {
            setup(event.getGuildId().get().asString());
            event.getMessage().getChannel().block().createMessage("volume is : " + player.getVolume()).block();
        });
        commands.put("help", event -> {
            String helpStr =
                    "mroom PARAM          --> sets the music room\n" +
                            "play PARAM                --> plays the music (Youtube link or keywords.)\n" +
                            "stop,pause                    --> pauses the music\n" +
                            "skip                                 --> plays next music if exist\n" +
                            "cont                                --> music continues\n" +
                            "setvol PARAM             --> sets the volume.\n" +
                            "getvol                             --> gets the volume\n" +
                            "mov PARAM(in ms)   --> moves the song to that millisecond\n" +
                            "bw,fw                             --> backward or forward for 60000ms\n" +
                            "li                                      --> prints next song list\n" +
                            "del PARAM                  --> deletes messages\n" +
                            "olddel                            --> deletes 2 week older messages slowly.\n" +
                            "heykır PARAM            --> to change prefix\n" +
                            "stub                               --> to get a new synctube room\n" +
                            "lk                                    --> to get total message count. \n" +
                            "eksi                                --> to get turkish news from eksi sozluk\n" +
                            "for any math work ${MATH} use that syntax.";
            event.getMessage().getChannel().block().createMessage(":\nCommands:\n" + helpStr).block();
        });
        commands.put("heykır", event -> {
            SYSTEM_PREFIX_PROPERTY = event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" ")).replace(" ", "");
        });
        commands.put("stub", event -> {
            event.getMessage().getChannel().block().createMessage(":\nA sync-tube room has created : " + WebHandler.getSyncTubePage()).block();

        });
        commands.put("eksi", event -> {
            event.getMessage().getChannel().block().createEmbed(spec ->
                    //spec.setColor(Color.of((float) Math.random(), (float) Math.random(), (float) Math.random()))
                    spec.setColor(Color.DARK_GRAY)
                            .setTitle("Eksi Sözlük Gündem : ")
                            .setDescription(WebHandler.eksiSozlukGundem())
            ).block();
            /**
             //further updates.
             System.out.println(event.getMessage().getChannel().block().getId().asString()); //getting music chanells id
             event.getClient().getChannelById(Snowflake.of("803329049526796298")).block().getRestChannel().createMessage("asd").block(); //adjusting to text there.
             */
        });
        commands.put("mroom", event -> {
            //musicRoomId = event.getMessage().getChannel().block().getId().asString();
            //System.out.println(event.getGuildId().get().asString());
            System.out.println("mroom çalıştı");
            System.out.println("guild id : " + event.getGuildId().get().asString() + " file handlera gönderildi");
            System.out.println("channel id : " + event.getMessage().getChannel().block().getId().asString() + " file handlera gönderildi");
            try {
                FileHandler.writeToFile(event.getGuildId().get().asString(), event.getMessage().getChannel().block().getId().asString());
            }catch (Exception e){}
        });

    }

    private static boolean isLink(String str) {
        return str.substring(0, 4).equals("http");
    }

    private static AudioProvider getProvider(String guildId){
        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        // This is an optimization strategy that Discord4J can utilize. It is not important to understand
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);
        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();
        final TrackScheduler ts = new TrackScheduler(player);

        PlayerSchedulerHolder.put(guildId, player, ts);
        // We will be creating LavaPlayerAudioProvider in the next step
        AudioProvider provider = new LavaPlayerAudioProvider(player);
        return provider;
    }

    private static void setup(String guildId){
        PlayerSchedulerHolder holder = PlayerSchedulerHolder.guildToPlayerMap.get(guildId);
        CommandHandler.player = holder.getPlayer();
        CommandHandler.scheduler = holder.getScheduler();
        CommandHandler.audioPlayStack = scheduler.audioPlayStack;
        CommandHandler.isLooped = scheduler.isLooped();
    }

}
