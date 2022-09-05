package com.jl.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
