package com.jl.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;


import static org.bukkit.Bukkit.getLogger;

public class ChestOpenListener implements Listener {

    private CustomMap<Location, Inventory> inventories;
    private Tombstone tombstone;
    public ChestOpenListener(CustomMap<Location, Inventory> inventories, Tombstone tombstone){
        super();
        this.inventories = inventories;
        this.tombstone = tombstone;
    }

    @EventHandler
    public void onInvOpen(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.CHEST) {
                Block block = event.getClickedBlock();
                Location blockLoc = block.getLocation();
                Inventory inv = inventories.get(blockLoc);
                if(inv != null){
                    event.setCancelled(true);
                    if(event.getPlayer().equals(inv.getHolder())){
                        event.getPlayer().openInventory(inv);
                        tombstone.saveMap();
                    }
                }
            }
        }
    }
}


