package com.jl.main;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.mana.MAbility;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;

import static java.lang.Thread.sleep;
import static org.bukkit.Bukkit.getLogger;


public class FlightListener implements Listener {
    private Tombstone tombstone;

    private HashMap<Player, Boolean> allowFlight;

    public FlightListener(Tombstone tombstone){
        this.tombstone = tombstone;
        allowFlight = new HashMap<Player, Boolean>();
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void playerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        
        Boolean allowed = allowFlight.get(player);
        if(allowed == null){
            allowFlight.put(player, true);
        }else{
            if(!allowed.booleanValue()){
                return;
            }
        }
        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

        double manaCost = Tombstone.MANA_COST;
        if (Tombstone.SCALABLE) {
            manaCost = AureliumAPI.getManaRegen(player) + Tombstone.MANA_COST;
        }

        PlayerData playerData = tombstone.getAureliumPlugin().getPlayerManager().getPlayerData(player);
        if (event.isFlying()) {

            double finalManaCost = manaCost;
            tombstone.getServer().getScheduler().runTaskAsynchronously(tombstone, () -> {
                playerData.getAbilityData(MAbility.ABSORPTION).setData("activated", true);
                if (AureliumAPI.getMana(player) >= finalManaCost) {
                    player.setFlying(true);
                }
                boolean oom = false;

                while (AureliumAPI.getMana(player) > 0 && player.isFlying()) {

                    double mana = AureliumAPI.getMana(player) - finalManaCost;
                    if (mana <= 0) {
                        mana = 0;
                        AureliumAPI.setMana(player, mana);
                        oom = true;
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
                playerData.getAbilityData(MAbility.ABSORPTION).setData("activated", false);
                allowFlight.put(player, false);

                if(oom) {
                    tombstone.getServer().getScheduler().runTaskLaterAsynchronously(tombstone, () -> {
                        allowFlight.put(player, true);
                        player.setAllowFlight(true);
                    }, 1 * (20));
                    return;
                }else{
                    allowFlight.put(player, true);
                    player.setAllowFlight(true);
                }
            });
        } else {
            event.getPlayer().setFlying(false);
            playerData.getAbilityData(MAbility.ABSORPTION).setData("activated", false);
        }

    }

}
