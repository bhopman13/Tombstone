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
    private boolean skills = false;
    private AureliumSkills skills_plugin;
    private BukkitTask task;
    HashMap<String, Double> multipliers = new HashMap<>();
    HashMap<String, BukkitTask> tasks = new HashMap<>();
    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        storage = new Storage(this);
        CustomMap<Location, Inventory> inventories = storage.loadMap();

        getServer().getPluginManager().registerEvents(new DeathListener(inventories, this, multipliers), this);
        getServer().getPluginManager().registerEvents(new ChestOpenListener(inventories, this), this);
        getServer().getPluginManager().registerEvents(new ChestCloseListener(inventories, this), this);
        getServer().getPluginManager().registerEvents(new ChestBreakListener(inventories, this), this);

        if(getServer().getPluginManager().getPlugin("AureliumSkills") != null){
            skills = true;
            skills_plugin = AureliumAPI.getPlugin();

            try {
                loadMults();
            } catch (IOException e) {
                skills = false;
                return;
            }
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, multipliers, tasks), this);
            getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this, tasks), this);
            saveMultsTask();

        }

    }
    FileConfiguration customConfig;
    File customYml;
    private void loadMults() throws IOException {

        try {
            customYml = new File(getDataFolder() + "/multipliers.dat");
            customConfig = YamlConfiguration.loadConfiguration(customYml);
            multipliers = (HashMap<String, Double>) customConfig.get("multipliers");
        }catch(Exception e){
            customYml = new File(getDataFolder() + "/multipliers.dat");
            customYml.delete();
            customYml.createNewFile();
            customConfig = YamlConfiguration.loadConfiguration(customYml);
        }
    }
    private void saveMultsTask(){
        BukkitScheduler scheduler = getServer().getScheduler();
        task = scheduler.runTaskTimerAsynchronously(this, () -> {
            saveMults();
        }, 12000L, 12000L);
    }
    private void saveMults(){
        customConfig.set("multipliers", multipliers);
        try {
            customConfig.save(customYml);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error saving multipliers");
        }
    }

    public void saveMap() {
        storage.writeToFile();
    }

    @Override
    public void onDisable() {
        saveMap();
        if(task != null) {task.cancel();}
        saveMults();
        getLogger().info("Tombstone chests saved. Closing down.");
    }

    public boolean isAureliumLoaded(){
        return skills;
    }

    public AureliumSkills getAureliumPlugin(){
        return skills_plugin;
    }

}
