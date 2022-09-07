package com.jl.main;

import com.archyx.aureliumskills.api.AureliumAPI;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import static java.lang.Thread.sleep;
import static org.bukkit.Bukkit.getLogger;


public class FlightListener implements Listener {
    private Tombstone tombstone;
    public FlightListener(Tombstone tombstone){
        this.tombstone = tombstone;

    }
    @EventHandler(priority = EventPriority.HIGH)
    public void playerToggleFlight(PlayerToggleFlightEvent event){
        Player player = event.getPlayer();

        if(!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
            return;
        }

        double manaCost = Tombstone.MANA_COST;
        if(Tombstone.SCALABLE){
            manaCost = AureliumAPI.getManaRegen(player) + Tombstone.MANA_COST;
        }

        if(event.isFlying()) {
            tombstone.getAureliumPlugin();
            double finalManaCost = manaCost;
            tombstone.getServer().getScheduler().runTaskAsynchronously(tombstone, () -> {

                if(AureliumAPI.getMana(player) >= finalManaCost) {
                    player.setFlying(true);
                }

                while (AureliumAPI.getMana(player) > 0 && player.isFlying()){
                    double mana = AureliumAPI.getMana(player) - finalManaCost;
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
                player.setAllowFlight(false);
            });
        }else{
            event.getPlayer().setFlying(false);
        }

        tombstone.getServer().getScheduler().scheduleSyncDelayedTask(tombstone, ()-> {
            event.getPlayer().setAllowFlight(true);
            //event.getPlayer().getServer().sett
        }, 60L);
    }

}
