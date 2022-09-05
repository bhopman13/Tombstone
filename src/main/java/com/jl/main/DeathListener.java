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

    public DeathListener(CustomMap<Location, Inventory> inventories, Tombstone tombstone) {
        super();
        this.inventories = inventories;
        this.tombstone = tombstone;
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

            Skill s = Skills.getOrderedValues().get(new Random().nextInt(Skills.getOrderedValues().size()));
            while (s == Skills.ENDURANCE){
                s = Skills.getOrderedValues().get(new Random().nextInt(Skills.getOrderedValues().size()));
            }
            if(new Random().nextInt(100) == 69){
                s = Skills.ENDURANCE;
            }
            boolean endurance = reduceSkillLevel(player, Skills.ENDURANCE);
            boolean random = reduceSkillLevel(player, s);
            if(random){
                player.sendMessage(s.toString() + " was chosen randomly :((");
            }else{
                player.sendMessage(s.toString() + " was chosen randomly but the level was already 0");
            }
            if(endurance){
                player.sendMessage("Endurance reduced by 1, death chest is allowed");
            }else{
                if(random) {
                    player.sendMessage("Endurance was too low but " + s +" was not, death chest is allowed");
                }else{
                    player.sendMessage("Levels could not be taken, death chest is not allowed :(");
                    return;
                }
            }

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

    private boolean reduceSkillLevel(Player player, Skill skill){
        AureliumSkills plugin = tombstone.getAureliumPlugin();
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return false;
        int oldLevel = playerData.getSkillLevel(skill);
        if(oldLevel <= 1) return false;
        playerData.setSkillLevel(skill, oldLevel - 1);
        playerData.setSkillXp(skill, 0);
        plugin.getLeveler().updateStats(player);
        plugin.getLeveler().updatePermissions(player);
        plugin.getLeveler().applyRevertCommands(player, skill, oldLevel, oldLevel - 1);
        plugin.getLeveler().applyLevelUpCommands(player, skill, oldLevel, oldLevel - 1);
        // Reload items and armor to check for newly met requirements
        plugin.getModifierManager().reloadPlayer(player);
        return true;
    }
}
