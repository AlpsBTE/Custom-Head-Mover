package com.alpsbte.customheadmover.commands;

import com.alpsbte.customheadmover.CustomHeadMover;
import com.alpsbte.customheadmover.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoadHeadsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("alpsbte.loadheads")) {
                CustomHeadMover.getPlugin().getNmsHandler().loadCustomHeads((Player) sender);
            } else sender.sendMessage(Utils.getErrorMessageFormat("You don't have permissions to execute this command!"));
        }
        return true;
    }
}
