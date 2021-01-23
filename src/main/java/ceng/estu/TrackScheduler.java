package ceng.estu;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Stack;

/**
 * @author reuzun
 */
public final class TrackScheduler extends AudioEventAdapter implements AudioLoadResultHandler {

    static String addedSongName = "";
    static Stack<AudioTrack> audioPlayStack = new Stack<>();
    public static AudioPlayer player = null;

    public TrackScheduler(final AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void trackLoaded(final AudioTrack track) {
        System.out.println("trackLoaded");
        // LavaPlayer found an audio source for us to play
        if(player.getPlayingTrack() != null){
            audioPlayStack.push(track);
            return;
        }
        player.playTrack(track);
        while(player.getPlayingTrack() != null){
            try {
                Thread.sleep(track.getDuration()+500);
            }catch (Exception e){
                //do nothing
                System.out.println("HATA ALDIK GARİPTİR Kİ ");
            }
        }
        System.out.println("ŞARKI BİTTİ");
        if (!audioPlayStack.isEmpty())
            trackLoaded(audioPlayStack.pop());
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist) {
        // LavaPlayer found multiple AudioTracks from some playlist
        System.out.println("PLAYLİSTLOADED");
        trackLoaded(playlist.getTracks().get(0));

    }

    @Override
    public void noMatches() {
        // LavaPlayer did not find any audio to extract
        System.out.println("NOMATCHES");
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        // LavaPlayer could not parse an audio source for some reason
        System.out.println("LOADFAİLED");
    }
}
