package com.jl.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UpdateCommand implements CommandExecutor {

    private Tombstone tombstone;
    public UpdateCommand(Tombstone tombstone){
        super();
        this.tombstone = tombstone;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            double mult = tombstone.getMultiplier(player);
            return tombstone.applyMult(player, mult);
        }
        return false;
    }
}
