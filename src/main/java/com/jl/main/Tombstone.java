package com.jl.main;


import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;

import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.modifier.Multiplier;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;

public class Tombstone extends JavaPlugin {

    public static long DELAY = 120L;
    private Storage storage;
    private boolean skills = false;
    private AureliumSkills skills_plugin;
    private BukkitTask task;
    HashMap<String, Double> multipliers;
    HashMap<String, BukkitTask> tasks;
    public static double MULT_INC = 0;
    public static double MANA_COST = 0;
    public static boolean SCALABLE = true;
    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        storage = new Storage(this);
        tasks = new HashMap<>();
        multipliers = new HashMap<>();
        CustomMap<Location, Inventory> inventories = storage.loadMap();
        genConfig();
        loadConfig();
        this.getCommand("ts").setExecutor(new UpdateCommand(this));
        getServer().getPluginManager().registerEvents(new FlightListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new GamemodeListener(this), this);
        getServer().getPluginManager().registerEvents(new ChestOpenListener(inventories, this), this);
        getServer().getPluginManager().registerEvents(new ChestCloseListener(inventories, this), this);
        getServer().getPluginManager().registerEvents(new ChestBreakListener(inventories, this), this);
        getServer().getPluginManager().registerEvents(new PlayerSpawnListener(this), this);
        if(getServer().getPluginManager().getPlugin("AureliumSkills") != null){
            getLogger().info("Aurelium skills found");
            skills = true;
            skills_plugin = AureliumAPI.getPlugin();

            try {
                loadMults();
            } catch (IOException e) {
                skills = false;
                return;
            }
            getServer().getPluginManager().registerEvents(new DeathListener(inventories, this, multipliers), this);
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, multipliers, tasks), this);
            getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this, tasks), this);
            saveMultsTask();
        }else{
            getServer().getPluginManager().registerEvents(new DeathListener(inventories, this), this);
        }

    }

    private void genConfig(){
        try {
            File configYml = new File(getDataFolder() + "/config.yml");
            configYml.delete();
            configYml.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(configYml);
            config.set("multiplier_increment", 1.0);
            config.set("mana_cost", 3.0);
            config.set("scalable", true);
            config.set("comment", "If scalable is true the mana cost per second of flight is mana_regen+mana_cost");
            config.set("delay", 120L);
            config.save(configYml);
            MULT_INC = 1;
            MANA_COST = 3;
            SCALABLE = true;
            DELAY = 120L;
        }catch (Exception e){

        }
    }
    private void loadConfig(){
        File configYml = new File(getDataFolder() + "/config.yml");
        if(!configYml.exists()){
            genConfig();
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configYml);
        String mult = config.getString("multiplier_increment");
        if(mult == null){
            genConfig();
            return;
        }else{
            try {
                MANA_COST = Double.parseDouble(config.getString("mana_cost"));
                MULT_INC = Double.parseDouble(mult);
                SCALABLE = config.getBoolean("scalable");
                DELAY = config.getLong("delay");
            }catch (Exception e){
                getLogger().info("Couldnt load mult inc or mana cost, set to default");
                MULT_INC = 1;
                MANA_COST = 3;
                SCALABLE = true;
                DELAY = 120L;
                genConfig();
            }
        }
    }

    FileConfiguration customConfig;
    File customYml;
    private void loadMults() throws IOException {
        multipliers = new HashMap<>();
        try {
            customYml = new File(getDataFolder() + "/multipliers.dat");
            customConfig = YamlConfiguration.loadConfiguration(customYml);

            String input = "";
            int i = 0;
            while((input = customConfig.getString(i+"")) != null){
                String uuid = customConfig.getString(i+".uuid");
                double mult = Double.parseDouble(customConfig.getString(i+".mult"));
                getLogger().info("Loading value: " + uuid);
                multipliers.put(uuid, mult);
                getLogger().info(multipliers.size()+"");
                i++;
            }
        }catch(Exception e){
            e.printStackTrace();
            customYml = new File(getDataFolder() + "/multipliers.dat");
            customYml.delete();
            customYml.createNewFile();
            customConfig = YamlConfiguration.loadConfiguration(customYml);
        }

    }
    private void saveMultsTask(){
        BukkitScheduler scheduler = getServer().getScheduler();
        task = scheduler.runTaskTimerAsynchronously(this, () -> saveMults(), 12000L, 12000L);
    }
    private void saveMults(){
        int i = 0;
        for(String uuid : multipliers.keySet()){
            double mult = multipliers.get(uuid).doubleValue();
            customConfig.set(i+"", "");
            customConfig.set(i+".uuid", uuid);
            customConfig.set(i+".mult", mult);
            i++;
        }

        try {
            customConfig.save(customYml);
            getLogger().info("Saved multiplier data");
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
        if(isAureliumLoaded()){
            saveMults();
        }
        getLogger().info("Tombstone chests saved. Closing down.");
    }

    public boolean isAureliumLoaded(){
        return skills;
    }

    public AureliumSkills getAureliumPlugin(){
        return skills_plugin;
    }

    public double getMultiplier(Player player) {
        if(multipliers == null) getLogger().info("Multipliers is null");
        Double mult = multipliers.get(player.getUniqueId().toString());
        if(mult == null){
            multipliers.put(player.getUniqueId().toString(), 1.0);
            return 1.0;
        }
        return mult.doubleValue();
    }

    public void setMultiplier(Player player, double mult){
        multipliers.put(player.getUniqueId().toString(), mult);
    }

    public boolean applyMult(Player player, double mult){
        PlayerData playerData = getAureliumPlugin().getPlayerManager().getPlayerData(player);
        if(playerData == null){
            return false;
        }
        playerData.addMultiplier(new Multiplier("jl_alive_multiplier", null, mult));
        return true;
    }

}
