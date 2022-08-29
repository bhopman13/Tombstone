package com.jl.main;

import org.bukkit.inventory.Inventory;

import java.io.Serializable;
import java.util.ArrayList;

import static org.bukkit.Bukkit.getLogger;


public class CustomMap<K, V> implements Serializable {

    private ArrayList<K> keys;
    private ArrayList<V> values;

    public CustomMap(){
        keys = new ArrayList<K>();
        values = new ArrayList<V>();
    }

    public void put(K key, V value){
        keys.add(key);
        values.add(value);
    }

    public V get(K key){
        for(int i = 0; i < keys.size(); i++){
            getLogger().info(keys.get(i).toString());

            if(keys.get(i).equals(key)){
                return values.get(i);
            }
        }
        return null;
    }


    public K findValue(V value) {
        K res = null;
        for(int i = 0; i < values.size(); i++){
            if(values.get(i) == value){
                res = keys.get(i);
                break;
            }
        }
        return res;
    }

    public void remove(K key) {
        int pos = -1;
        for(int i = 0; i < keys.size(); i++){
            if (keys.get(i).equals(key)){
                pos = i;
                break;
            }
        }
        if(pos >= 0){
            keys.remove(pos);
            values.remove(pos);
        }
    }

    public ArrayList<K> getKeys(){
        return keys;
    }

    public V get(int i) {
        return values.get(i);
    }


}
