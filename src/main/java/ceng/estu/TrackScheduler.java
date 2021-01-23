package ceng.estu;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author reuzun
 */
public final class TrackScheduler implements AudioLoadResultHandler {

    //static ArrayList<AudioTrack> list = new ArrayList<>();

    static Stack<AudioTrack> audioPlayStack = new Stack<>();
    public static AudioPlayer player = null;


    public TrackScheduler(final AudioPlayer player) {

        this.player = player;

        player.addListener(new AudioEventListener() {
            @Override
            public void onEvent(AudioEvent event) {
                System.out.println("An event has occured.");
                if(!audioPlayStack.isEmpty())
                    trackLoaded(audioPlayStack.pop());
                else
                    System.out.println("Stack is emty for listener");
            }
        });
    }

    @Override
    public void trackLoaded(final AudioTrack track) {
        // LavaPlayer found an audio source for us to play
        //list.add(track);
        //if(player.getPlayingTrack() != null)

        if(player.getPlayingTrack() != null){
            audioPlayStack.push(track);
            return;
        }

        player.playTrack(track);

        /*while(player.getPlayingTrack() != null){
            //do nothing. This for waiting end of song.
        }*/

        if(!audioPlayStack.isEmpty()) {
            trackLoaded(audioPlayStack.pop());
        }
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist) {
        // LavaPlayer found multiple AudioTracks from some playlist
        trackLoaded(playlist.getTracks().get(0));
    }

    @Override
    public void noMatches() {
        // LavaPlayer did not find any audio to extract
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        // LavaPlayer could not parse an audio source for some reason
    }
}
