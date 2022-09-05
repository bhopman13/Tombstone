package com.jl.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class PlayerLeaveListener implements Listener {

    Tombstone tombstone;
    HashMap<String, BukkitTask> tasks;
    public PlayerLeaveListener(Tombstone tombstone, HashMap<String, BukkitTask> tasks) {
        this.tombstone = tombstone;
        this.tasks = tasks;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BukkitTask task = tasks.get(player.getUniqueId().toString());
        if(task != null){
            task.cancel();
        }
        tasks.remove(player.getUniqueId().toString());

    }
}
