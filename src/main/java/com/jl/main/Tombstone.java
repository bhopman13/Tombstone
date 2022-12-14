package com.jl.main;


import org.bukkit.Location;


import org.bukkit.inventory.Inventory;

import org.bukkit.plugin.java.JavaPlugin;


public class Tombstone extends JavaPlugin {

    private Storage storage;
    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        storage = new Storage(this);
        CustomMap<Location, Inventory> inventories = storage.loadMap();

        getServer().getPluginManager().registerEvents(new DeathListener(inventories, this), this);
        getServer().getPluginManager().registerEvents(new ChestOpenListener(inventories, this), this);
        getServer().getPluginManager().registerEvents(new ChestCloseListener(inventories, this), this);
        getServer().getPluginManager().registerEvents(new ChestBreakListener(inventories, this), this);



    }

    public void saveMap() {
        storage.writeToFile();
    }

    @Override
    public void onDisable() {
        saveMap();
        getLogger().info("Tombstone chests saved. Closing down.");
    }



}
