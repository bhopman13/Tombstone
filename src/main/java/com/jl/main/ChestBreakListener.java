package com.jl.main;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


import static org.bukkit.Bukkit.getLogger;

public class ChestBreakListener implements Listener {
    private CustomMap<Location, Inventory> inventories;
    private Tombstone tombstone;
    public ChestBreakListener(CustomMap<Location, Inventory> inventories, Tombstone tombstone){
        super();
        this.inventories = inventories;
        this.tombstone = tombstone;
    }

    @EventHandler
    public void onChestBreak(BlockBreakEvent event){
        if(event.getBlock().getType() != Material.CHEST) return;
        Block brokenBlock = event.getBlock();
        Location loc = event.getBlock().getLocation();
        Inventory inv = inventories.get(loc);
        if(inv != null){
            ItemStack[] contents = inv.getContents();
            event.setDropItems(false);
            brokenBlock.getDrops().clear();
            for(ItemStack stack : contents){
                if(stack != null) {
                    brokenBlock.getWorld().dropItemNaturally(brokenBlock.getLocation(), stack);
                }
            }
            inventories.remove(loc);
            tombstone.saveMap();
        }


    }
}
