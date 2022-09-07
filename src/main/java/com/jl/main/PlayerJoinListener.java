package com.jl.main;

import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.modifier.Multiplier;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerJoinListener implements Listener {
    Tombstone tombstone;
    HashMap<String, Double> multipliers;
    HashMap<String, BukkitTask> tasks;
    public PlayerJoinListener(Tombstone tombstone, HashMap<String, Double> multipliers, HashMap<String, BukkitTask> tasks){
        this.tombstone = tombstone;
        this.multipliers = multipliers;
        this.tasks = tasks;
    }
    long ticks = 24000L;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        if(player.getName().contains("panda")){
            if(new Random().nextInt(5) == 3) {
                tombstone.getServer().getScheduler().scheduleSyncDelayedTask(tombstone, () -> {
                    player.getWorld().spawnEntity(player.getLocation(), EntityType.PANDA);
                    player.sendMessage(ChatColor.GOLD + "PANDA!");
                    player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, SoundCategory.AMBIENT,100, 1);
                }, 100L);
            }
        }

        tombstone.getServer().getScheduler().scheduleSyncDelayedTask(tombstone, () -> {
            if(tombstone.applyMult(event.getPlayer(), getMult(event.getPlayer()))){
                event.getPlayer().sendMessage("Applied your multiplier: " + (1 + (getMult(event.getPlayer())/100))+"x");
            }
            //tombstone.getAureliumPlugin().getPlayerManager().getPlayerData(event.getPlayer()).addMultiplier(new Multiplier("jl_alive_multiplier", null, getMult(event.getPlayer())));;
            BukkitScheduler scheduler = tombstone.getServer().getScheduler();
            String UUID = event.getPlayer().getUniqueId().toString();
            BukkitTask task = scheduler.runTaskTimerAsynchronously(tombstone, () -> {
                double mult = getMult(event.getPlayer());
                if(mult < 300.0) {
                    mult += Tombstone.MULT_INC;
                    multipliers.put(UUID, mult);
                }

                tombstone.applyMult(event.getPlayer(), mult);
            }, ticks, ticks);
            tasks.put(UUID, task);

        }, 200);

    }


    private double getMult(Player player){
        String UUID = player.getUniqueId().toString();
        Double mult = multipliers.get(UUID);
        if(mult == null){
            multipliers.put(UUID, 1.0);
            mult = 1.0;
        }
        return mult;
    }

}

