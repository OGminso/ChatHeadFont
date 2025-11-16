package net.minso.chathead;

import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.Examples.ActionBarExample;
import net.minso.chathead.Examples.BossbarExample;
import net.minso.chathead.Examples.JoinLeaveChatExample;
import net.minso.chathead.Hooks.PlaceholderAPIHook;
import net.minso.chathead.Utils.UpdateChecker;
import net.minso.chathead.config.Config;
import net.minso.chathead.listener.PlayerListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Main extends JavaPlugin {
    public static final String RESOURCE_PACK = "https://github.com/OGminso/ChatHeadFont/raw/main/pack.zip";
    private Config config;

    @Override
    public void onEnable() {
        ChatHeadAPI.initialize(this);
        this.config = new Config(this);
        this.config.init();
        this.registerListeners();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            PlaceholderAPIHook.registerHook(this);
            getLogger().info("Hooked into PlaceholderAPI!");
        }

        //Uncomment this to enable the examples!
        //registerExamples();

        new UpdateChecker(this).checkForUpdates();

        Metrics metrics = new Metrics(this, 27972);
    }

    private void registerExamples() {
        getServer().getPluginManager().registerEvents(new ActionBarExample(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveChatExample(), this);
        getServer().getPluginManager().registerEvents(new BossbarExample(), this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public boolean isOfflineModeEnabled() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("server.properties"));

            return Boolean.parseBoolean(props.getProperty("online-mode", "true"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Config getPluginConfig() {
        return config;
    }
}
