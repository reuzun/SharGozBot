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

import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;


import static ceng.estu.main.SharGozBot.commands;
import static ceng.estu.main.SharGozBot.SYSTEM_PREFIX_PROPERTY;

import java.time.Instant;
import java.util.*;

/**
 * @author reuzun
 */
class CommandHandler {

    static boolean bool = true;
    static boolean isPlaylist = false;

    protected static void initializeCommands(AudioProvider provider, AudioPlayerManager playerManager, AudioPlayer player) {
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
            if (!((Stack<AudioTrack>) TrackScheduler.audioPlayStack.clone()).isEmpty())
                for (AudioTrack t : (Stack<AudioTrack>) TrackScheduler.audioPlayStack.clone()) {
                    if (!t.getInfo().isStream)
                        event.getMessage().getChannel().block().createMessage("\n" + (i++) + ". song : " + t.getInfo().title + " Duration : " + t.getInfo().length + "ms").block();
                    else
                        event.getMessage().getChannel().block().createMessage("\n" + (i++) + ". song : " + t.getInfo().title + " Duration : " + "Live Stream").block();
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
            //for (int i = 0; i < 10; i++) {
            try {
                  /*  /*List<Message> messageList =  event.getMessage().getChannel().block().getMessagesBefore(Snowflake.of(Instant.now())).collectList().block();
                    System.out.println("size is : " + messageList.size());
                    for(int i = 0; i < 15 ; i++){
                        System.out.println(messageList.get(i).getContent());
                        messageList.get(i).delete().block();
                    }
                     */
                    /*System.out.println("Started");
                    List<Message> messageList =  event.getMessage().getChannel().block().getMessagesBefore(Snowflake.of(Instant.now())).collectList().block();
                    System.out.println("Ended");

                    System.out.println("Size is : " + messageList.size());
                    event.getMessage().getChannel().block().createMessage("There is : " + messageList.size() +  " Messages on this channel.").block();
                    event.getMessage().getChannel().block().createMessage("First message is :" + messageList.get(messageList.size()-1).getContent()).block();
                    event.getMessage().getChannel().block().createMessage("Last message is : " + messageList.get(0).getContent()).block();

                    ListIterator<Message> iter = messageList.listIterator();

                    while(iter.hasNext()){
                        iter.next().delete().block();
                    }

                     */

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
            //}
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
        commands.put("help", event -> {
            String helpStr =
                    "join          --> joins to channel\n" +
                            "play          --> plays the music (Youtube link or keywords.)\n" +
                            "stop,pause    --> pauses the music\n" +
                            "skip          --> plays next music if exist\n" +
                            "cont          --> music continues\n" +
                            "setvol        --> sets the volume.\n" +
                            "mov           --> moves the song to that millisecond\n" +
                            "li            --> prints next song list\n" +
                            "del           --> deletes messages\n" +
                            "heykır        --> to change prefix\n" +
                            "st            --> to get a new synctube room\n" +
                            "lk           --> to get total message count. \n" +
                            "for any math work ${MATH} use that syntax.";
            StringBuilder sb = new StringBuilder();
            /*for(Map.Entry<String, Command> s : commands.entrySet()){
                sb.append(s.getKey()+"\n");
            }*/
            event.getMessage().getChannel().block().createMessage(":\nCommands:\n" + helpStr).block();
        });
        commands.put("heykır", event -> {
            SYSTEM_PREFIX_PROPERTY = event.getMessage().getContent().substring(event.getMessage().getContent().lastIndexOf(" ")).replace(" ", "");
        });
        commands.put("stub", event -> {
            event.getMessage().getChannel().block().createMessage(":\nA sync-tube room has created : " + WebHandler.getSyncTubePage()).block();

        });

    }

    private static boolean isLink(String str) {
        return str.substring(0, 4).equals("http");
    }


}
