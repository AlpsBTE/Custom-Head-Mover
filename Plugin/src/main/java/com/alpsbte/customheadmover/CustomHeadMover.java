package com.alpsbte.customheadmover;

import com.alpsbte.customheadmover.commands.LoadHeadsCMD;
import com.alpsbte.customheadmover.commands.ReloadPluginCMD;
import com.alpsbte.customheadmover.commands.SaveHeadsCMD;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

public class CustomHeadMover extends JavaPlugin {
    private static CustomHeadMover customHeadMoverPlugin;

    private FileConfiguration config;
    private File configFile;

    private INMSHandler nmsHandler;

    @Override
    public void onEnable() {
        customHeadMoverPlugin = this;
        reloadConfig();

        SkullCreator.headsFile = Paths.get(getDataFolder().getAbsolutePath(), "heads.json").toFile();

        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            Bukkit.getLogger().log(Level.INFO, version);
            final Class<?> clazz = Class.forName("com.alpsbte.customheadmover." + version + ".NMSHandler");
            // Check if we have a NMSHandler class at that location.
            if (INMSHandler.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.nmsHandler = (INMSHandler) clazz.getConstructor().newInstance(); // Set our handler
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            this.getLogger().severe("Could not find support for this CraftBukkit version.");
            this.setEnabled(false);
            return;
        }
        this.getLogger().info("Loading support for " + version);

        Objects.requireNonNull(this.getCommand("saveheads")).setExecutor(new SaveHeadsCMD());
        Objects.requireNonNull(this.getCommand("loadheads")).setExecutor(new LoadHeadsCMD());
        Objects.requireNonNull(this.getCommand("chmreload")).setExecutor(new ReloadPluginCMD());

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully enabled Custom-Head-Mover plugin.");
    }

    @Override
    public void reloadConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            // Look for default configuration file
            Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(this.getResource("config.yml")), StandardCharsets.UTF_8);
            config = YamlConfiguration.loadConfiguration(defConfigStream);
        }

        saveConfig();
        SkullCreator.pasteOffset = getConfig().getInt("paste-offset");
    }

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    @Override
    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }

        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public INMSHandler getNmsHandler() {
        return nmsHandler;
    }

    public static CustomHeadMover getPlugin() {
        return customHeadMoverPlugin;
    }
}
