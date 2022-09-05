package com.jl.main;


import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Chest;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getLogger;

public class DeathListener implements Listener {
    private CustomMap<Location, Inventory> inventories;
    private Tombstone tombstone;
    private HashMap<String, Double> multipliers;
    public DeathListener(CustomMap<Location, Inventory> inventories, Tombstone tombstone, HashMap<String, Double> multipliers) {
        super();
        this.inventories = inventories;
        this.tombstone = tombstone;
        this.multipliers = multipliers;
    }

    private boolean reduceSkillLevel(Player player, Skill skill, int amt){
        PlayerData data = tombstone.getAureliumPlugin().getPlayerManager().getPlayerData(player);
        if(data.getSkillLevel(skill) - amt < 0){
            return false;
        }
        if(data.getSkillLevel(skill) >= amt){
            data.setSkillLevel(skill, data.getSkillLevel(skill) - amt);
        }
        return true;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> drops = event.getDrops();
        ItemStack[] dropsArr = new ItemStack[drops.size()];
        for (int i = 0; i < drops.size(); i++) {
            dropsArr[i] = drops.get(i);
        }

        //should create chest??
        if(tombstone.isAureliumLoaded()) {
            multipliers.put(player.getUniqueId().toString(), 1.0);

            AureliumSkills skills = tombstone.getAureliumPlugin();
            Skill s = Skills.getOrderedValues().get(new Random().nextInt(Skills.getOrderedValues().size()));
            while (s == Skills.ENDURANCE){
                s = Skills.getOrderedValues().get(new Random().nextInt(Skills.getOrderedValues().size()));
            }
            if(new Random().nextInt(100) == 69){
                s = Skills.ENDURANCE;
            }

            if(!(reduceSkillLevel(player, Skills.ENDURANCE, 1) | reduceSkillLevel(player, s, 1))){
                player.sendMessage("Not enough XP for the chest XD");
                return;
            }
            player.sendMessage(s.toString() + " was chosen randomly :((");
        }


        Inventory deathInventory = Bukkit.createInventory(player, 54);
        deathInventory.setContents(dropsArr);
        Location deathLoc = player.getLocation().getBlock().getLocation();
        deathLoc.getBlock().setType(Material.CHEST);

        if (deathLoc.getBlock().getState() instanceof Chest) {
            event.getDrops().clear();
            inventories.put(deathLoc.getBlock().getLocation(), deathInventory);
            player.sendMessage(ChatColor.GREEN + "Death chest at (x:y:z): " + deathLoc.getBlockX()+":"+deathLoc.getBlockY()+":"+deathLoc.getBlockZ());
            tombstone.saveMap();
        } else {
            player.sendMessage("Error while setting up chest, your items fell like normal :(");
        }
    }
}
