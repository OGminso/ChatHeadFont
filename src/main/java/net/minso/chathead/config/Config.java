package net.minso.chathead.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private final JavaPlugin plugin;

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean getAutoDownloadPackEnabled() {
        return plugin.getConfig().getBoolean("auto-download-pack", true);
    }

    public boolean getJoinMessagesEnabled() {
        return plugin.getConfig().getBoolean("enable-join-messages", true);
    }

    public boolean getLeaveMessagesEnabled() {
        return plugin.getConfig().getBoolean("enable-leave-messages", true);
    }

    public boolean getChatMessagesEnabled() {
        return plugin.getConfig().getBoolean("enable-chat-messages", true);
    }

    public boolean getDeathMessagesEnabled() {
        return plugin.getConfig().getBoolean("enable-death-messages", true);
    }

    public void init() {
        FileConfiguration config = plugin.getConfig();
        //default configuration:
        config.addDefault("auto-download-pack", true);
        config.addDefault("enable-join-messages", true);
        config.addDefault("enable-leave-messages", true);
        config.addDefault("enable-chat-messages", true);
        config.addDefault("enable-death-messages", true);

        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();
    }
}
