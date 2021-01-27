package ceng.estu.main;

import ceng.estu.utilities.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author reuzun
 */
public class PlayerSchedulerHolder {
    private AudioPlayer player;
    private TrackScheduler scheduler;
    public static Map<String, PlayerSchedulerHolder> guildToPlayerMap = new HashMap();

    public PlayerSchedulerHolder(AudioPlayer player, TrackScheduler scheduler){
        this.player = player;
        this.scheduler = scheduler;
    }

    public static void put(String guildId, AudioPlayer player, TrackScheduler scheduler){
        PlayerSchedulerHolder holder = new PlayerSchedulerHolder(player, scheduler);
        guildToPlayerMap.put(guildId, holder);

        debug();
    }

    public AudioPlayer getPlayer(){
        return this.player;
    }

    public TrackScheduler getScheduler(){
        return scheduler;
    }

    public static void debug(){

        for(var a : guildToPlayerMap.entrySet())
            System.out.println("key is : " + a.getKey() + "\nplayer is : " + a.getValue().getPlayer() +"\nscheduler is : " + a.getValue().getScheduler()+"\n");
    }
}
