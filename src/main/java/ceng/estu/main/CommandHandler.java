package ceng.estu.main;

import ceng.estu.utilities.TrackScheduler;
import ceng.estu.webhandle.WebHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Color;
import discord4j.voice.AudioProvider;


import static ceng.estu.main.SharGozBot.commands;
import static ceng.estu.main.SharGozBot.SYSTEM_PREFIX_PROPERTY;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author reuzun
 */
class CommandHandler {

    static String musicRoomId = null;
    static boolean isPlaylist = false;

    protected static void initializeCommands(AudioProvider provider, AudioPlayerManager playerManager, AudioPlayer player) {

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
                if(musicRoomId == null) {
                    if (TrackScheduler.audioPlayStack.isEmpty() && TrackScheduler.player.getPlayingTrack() != null) {

                        if (!TrackScheduler.player.getPlayingTrack().getInfo().isStream)
                            event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + TrackScheduler.player.getPlayingTrack().getInfo().title +
                                    "\nAuthor of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().author +
                                    "\nDuration of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().length + "ms" +
                                    "\nLink of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().uri).block();
                        else
                            event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + TrackScheduler.player.getPlayingTrack().getInfo().title +
                                    "\nAuthor of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().author +
                                    "\nDuration of Song : " + "Live Stream" +
                                    "\nLink of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().uri).block();
                    } else { //non-first songs.
                        if (!TrackScheduler.audioPlayStack.peek().getInfo().isStream)
                            event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + TrackScheduler.audioPlayStack.peek().getInfo().title +
                                    "\nAuthor of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().author +
                                    "\nDuration of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().length + "ms" +
                                    "\nLink of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().uri).block();
                        else
                            event.getMessage().getChannel().block().createMessage(":\nPlaying song is : " + TrackScheduler.audioPlayStack.peek().getInfo().title +
                                    "\nAuthor of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().author +
                                    "\nDuration of Song : " + "Live Stream" +
                                    "\nLink of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().uri).block();
                    }
                }else {
                    if (TrackScheduler.audioPlayStack.isEmpty() && TrackScheduler.player.getPlayingTrack() != null) {

                        if (!TrackScheduler.player.getPlayingTrack().getInfo().isStream)
                            event.getClient().getChannelById(Snowflake.of(musicRoomId)).block().getRestChannel().createMessage(":\nPlaying song is : " + TrackScheduler.player.getPlayingTrack().getInfo().title +
                                    "\nAuthor of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().author +
                                    "\nDuration of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().length + "ms" +
                                    "\nLink of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().uri).block();
                        else
                            event.getClient().getChannelById(Snowflake.of(musicRoomId)).block().getRestChannel().createMessage(":\nPlaying song is : " + TrackScheduler.player.getPlayingTrack().getInfo().title +
                                    "\nAuthor of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().author +
                                    "\nDuration of Song : " + "Live Stream" +
                                    "\nLink of Song : " + TrackScheduler.player.getPlayingTrack().getInfo().uri).block();
                    } else { //non-first songs.
                        if (!TrackScheduler.audioPlayStack.peek().getInfo().isStream)
                            event.getClient().getChannelById(Snowflake.of(musicRoomId)).block().getRestChannel().createMessage(":\nPlaying song is : " + TrackScheduler.audioPlayStack.peek().getInfo().title +
                                    "\nAuthor of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().author +
                                    "\nDuration of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().length + "ms" +
                                    "\nLink of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().uri).block();
                        else
                            event.getClient().getChannelById(Snowflake.of(musicRoomId)).block().getRestChannel().createMessage(":\nPlaying song is : " + TrackScheduler.audioPlayStack.peek().getInfo().title +
                                    "\nAuthor of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().author +
                                    "\nDuration of Song : " + "Live Stream" +
                                    "\nLink of Song : " + TrackScheduler.audioPlayStack.peek().getInfo().uri).block();
                    }

                }
            } catch (Exception e) {
                System.out.println("e");
            }

        });
        commands.put("pause", event -> {
            player.setPaused(true);
            event.getMessage().getChannel().block().createMessage("Paused...").block();
        });
        commands.put("stop", event -> {
            player.setPaused(true);
            event.getMessage().getChannel().block().createMessage("Paused...").block();
        });
        commands.put("skip", event -> {
            TrackScheduler.isLooped = false; //diasble loop
            if (!TrackScheduler.audioPlayStack.isEmpty()) {
                TrackScheduler.player.playTrack(TrackScheduler.audioPlayStack.pop());
                event.getMessage().getChannel().block().createMessage("Next song will be playing ASAP.").block();
            } else
                event.getMessage().getChannel().block().createMessage("No Song is available.").block();
        });
        commands.put("cont", event -> {
            player.setPaused(false);
            event.getMessage().getChannel().block().createMessage("Playing...").block();
        });
        commands.put("setvol", event -> {
            player.setVolume(Integer.parseInt(event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" ")).replace(" ", "")));
            event.getMessage().getChannel().block().createMessage("Volume is set to : " + player.getVolume()).block();
        });
        commands.put("gvol", event -> {

            event.getMessage().getChannel().block().createMessage(String.valueOf(TrackScheduler.player.getVolume())).block();
        });
        commands.put("mov", event -> {
            player.getPlayingTrack().setPosition(Integer.parseInt(event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" ")).replace(" ", "")));
            try {
                Thread.sleep(225);
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.getMessage().getChannel().block().createMessage("Seeked to : " + player.getPlayingTrack().getPosition()).block();
        });
        commands.put("li", event -> {
            int i = 0;
            if (!((Stack<AudioTrack>) TrackScheduler.audioPlayStack.clone()).isEmpty()) {
                StringBuilder sb = new StringBuilder(":");
                for (AudioTrack t : (Stack<AudioTrack>) TrackScheduler.audioPlayStack.clone()) {
                    if (!t.getInfo().isStream)
                        sb.append("\n" + (i++) + ". song : " + t.getInfo().title + " Duration : " + t.getInfo().length + "ms");
                    else
                        sb.append("\n" + (i++) + ". song : " + t.getInfo().title + " Duration : " + "Live Stream");
                }
                event.getMessage().getChannel().block().createMessage(sb.toString()).block().delete().delaySubscription(Duration.ofMillis(3500)).block();
            }
            else
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
            TrackScheduler.isLooped = !TrackScheduler.isLooped;
            if (TrackScheduler.isLooped)
                event.getMessage().getChannel().block().createMessage("Loop enabled").block();
            else
                event.getMessage().getChannel().block().createMessage("Loop disabled").block();
        });
        commands.put("fw",event -> {
            TrackScheduler.player.getPlayingTrack().setPosition(TrackScheduler.player.getPlayingTrack().getPosition()+60000);
        });
        commands.put("bw",event -> {
            TrackScheduler.player.getPlayingTrack().setPosition(TrackScheduler.player.getPlayingTrack().getPosition()-60000);
        });
        commands.put("getvol",event -> {
            event.getMessage().getChannel().block().createMessage("volume is : " + TrackScheduler.player.getVolume()).block();
        });
        commands.put("help", event -> {
            String helpStr =
                            "mroom PARAM          --> sets the music room\n"+
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
            musicRoomId = event.getMessage().getChannel().block().getId().asString();
        });

    }

    private static boolean isLink(String str) {
        return str.substring(0, 4).equals("http");
    }


}
