package com.alpsbte.customheadmover.commands;

import com.alpsbte.customheadmover.CustomHeadMover;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SaveHeadsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            CustomHeadMover.getPlugin().getNmsHandler().saveCustomHeads((Player) sender);
        }
        return true;
    }
}
