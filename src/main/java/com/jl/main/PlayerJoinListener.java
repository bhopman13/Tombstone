package com.jl.main;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.modifier.Multiplier;
import com.archyx.aureliumskills.modifier.Multipliers;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    Tombstone tombstone;
    HashMap<String, Double> multipliers;
    HashMap<String, BukkitTask> tasks;
    public PlayerJoinListener(Tombstone tombstone, HashMap<String, Double> multipliers, HashMap<String, BukkitTask> tasks){
        this.tombstone = tombstone;
        this.multipliers = multipliers;
        this.tasks = tasks;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        BukkitScheduler scheduler = tombstone.getServer().getScheduler();
        String UUID = event.getPlayer().getUniqueId().toString();
        BukkitTask task = scheduler.runTaskTimerAsynchronously(tombstone, () -> {
            Double mult = multipliers.get(UUID);
            if(mult == null){
                multipliers.put(UUID, 1.0);
                return;
            }
            if(mult.doubleValue() == 3.0) {
                tombstone.getAureliumPlugin().getPlayerManager().getPlayerData(event.getPlayer()).addMultiplier(new Multiplier("jl_alive_multiplier", null, mult+0.05));
                return;
            };
            multipliers.put(UUID, mult+0.05);
        }, 24000L, 24000L);
        tasks.put(UUID, task);
    }

}

