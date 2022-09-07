package com.jl.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDamageListener implements Listener {

    private Tombstone tombstone;

    public PlayerDamageListener(Tombstone tombstone) {
        this.tombstone = tombstone;
    }

    @EventHandler
    public void playerDamageEvent(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            player.sendMessage(player.isFlying()+"f");
            player.sendMessage(player.getAllowFlight()+"af");
            if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
                if(player.getAllowFlight()){
                    event.setCancelled(false);
                }
            }
        }

    }

}
