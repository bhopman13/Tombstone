package com.jl.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;

public class ChestCloseListener implements Listener {
    private CustomMap<Location, Inventory> inventories;
    private Tombstone tombstone;
    public ChestCloseListener(CustomMap<Location, Inventory> inventories, Tombstone tombstone){
        super();
        this.inventories = inventories;
        this.tombstone = tombstone;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        Inventory closedInv = event.getInventory();
        Location location = inventories.findValue(closedInv);
        if(location == null) return;
        if(isInventoryEmpty(closedInv)){
            location.getBlock().setType(Material.AIR);
            inventories.remove(location);
        }

    }

    private boolean isInventoryEmpty(final Inventory inv) {
        return Arrays.stream(inv.getContents()).noneMatch(Objects::nonNull);
    }
}
