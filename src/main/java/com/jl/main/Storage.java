package com.jl.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;

import java.io.IOException;

import java.util.ArrayList;


import static org.bukkit.Bukkit.getLogger;

public class Storage  {
    private CustomMap<Location, Inventory> inventories;
    private FileConfiguration customConfig;
    private File customYml;
    private Plugin plugin;
    public Storage(Plugin plugin){
        this.plugin = plugin;
        try {
            customYml = new File(plugin.getDataFolder() + "/inventories.dat");
            customConfig = YamlConfiguration.loadConfiguration(customYml);
        }catch(Exception e){
            cleanYml();
        }
    }

    public void setInventories(CustomMap<Location, Inventory> inventories){
        this.inventories = inventories;
    }
    private boolean cleanYml(){
        boolean del = false;
        try {
            customYml = new File(plugin.getDataFolder() + "/inventories.dat");
            customYml.delete();
            del = customYml.createNewFile();
            customConfig = YamlConfiguration.loadConfiguration(customYml);
        }catch (Exception e){
            e.printStackTrace();
        }
        return del;
    }

    /**
     * @return true on success
     */
    public boolean writeToFile(){
        if(inventories == null) return false;

        cleanYml();
        ArrayList<Location> keys = inventories.getKeys();

        for(int i = 0; i < keys.size(); i++){
            customConfig.set(i+".location", keys.get(i));
            customConfig.set(i+".inventory", inventories.get(i).getContents());
            customConfig.set(i+".holder", inventories.get(i).getHolder());
        }
        try {
            customConfig.save(customYml);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CustomMap<Location, Inventory> loadMap() {
        inventories = new CustomMap<Location, Inventory>();
        int i = 0;

        Location loc = customConfig.getLocation(i+".location");
        InventoryHolder holder = customConfig.getObject(i+".holder", InventoryHolder.class);
        Inventory inv = getInv(holder,i+".inventory");
        while(loc != null && inv != null){
            inventories.put( loc,  inv);
            i++;
            loc = customConfig.getLocation(i+".location");
            holder = customConfig.getObject(i+".holder", InventoryHolder.class);
            inv = getInv(holder,i+".inventory");
        }
        getLogger().info(inventories.getKeys().size()+"");

        return inventories;
    }

    private Inventory getInv(InventoryHolder holder, String loc){
        ArrayList<ItemStack> content = (ArrayList<ItemStack>) customConfig.getList(loc);
        if(content == null) return null;

        ItemStack[] items = new ItemStack[content.size()];
        for (int i = 0; i < content.size(); i++) {
            ItemStack item = content.get(i);
            if (item != null) {
                items[i] = item;
            } else {
                items[i] = null;
            }
        }
        Inventory inv = Bukkit.createInventory(holder, 54);
        inv.setContents(items);
        return inv;
    }
}
