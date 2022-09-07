package com.jl.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GamemodeListener implements Listener {

    private Tombstone tombstone;

    public GamemodeListener(Tombstone tombstone) {
        this.tombstone = tombstone;
    }

    @EventHandler
    public void gamemodeListener(PlayerGameModeChangeEvent event){
        tombstone.getServer().getScheduler().scheduleSyncDelayedTask(tombstone, () -> {
            event.getPlayer().setAllowFlight(true);
        }, 60L);
    }
}
