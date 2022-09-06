package com.jl.main;

import com.archyx.aureliumskills.api.AureliumAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import static java.lang.Thread.sleep;
import static org.bukkit.Bukkit.getLogger;


public class FlightListener implements Listener {
    private Tombstone tombstone;
    public FlightListener(Tombstone tombstone){
        this.tombstone = tombstone;

    }
    @EventHandler
    public void playerToggleFlight(PlayerToggleFlightEvent event){
        Player player = event.getPlayer();
        event.setCancelled(true);
        if(event.isFlying()) {
            tombstone.getAureliumPlugin();
            tombstone.getServer().getScheduler().runTaskAsynchronously(tombstone, () -> {
                while (AureliumAPI.getMana(player) > 0){
                    if(!player.isFlying()){
                        player.setFlying(true);
                    }
                    double mana = AureliumAPI.getMana(player) - Tombstone.MANA_COST;
                    if(mana <= 0){
                        mana = 0;
                        AureliumAPI.setMana(player, mana);
                        break;
                    }
                    AureliumAPI.setMana(player, mana);

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                player.setFlying(false);
            });
        }else{
            event.getPlayer().setFlying(false);
        }
    }

}
