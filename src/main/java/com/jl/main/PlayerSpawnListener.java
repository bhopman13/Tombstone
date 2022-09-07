package com.jl.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class PlayerSpawnListener implements Listener {
    private Tombstone tombstone;

    public PlayerSpawnListener(Tombstone tombstone) {
        this.tombstone = tombstone;
    }

    @EventHandler
    public void playerSpawnListener(PlayerRespawnEvent event){
        tombstone.getServer().getScheduler().scheduleSyncDelayedTask(tombstone, () -> {
            event.getPlayer().setAllowFlight(true);
        }, 60L);
    }

}
