package com.jl.main;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.modifier.Multiplier;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.Location;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;


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
