package net.minso.chathead;

import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.Examples.ActionBarExample;
import net.minso.chathead.Examples.JoinLeaveChatExample;
import net.minso.chathead.Hooks.PlaceholderAPIHook;
import net.minso.chathead.config.Config;
import net.minso.chathead.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Main extends JavaPlugin {
    public static final String RESOURCE_PACK = "https://github.com/codingcat2468/ChatHeadFont/raw/main/pack.zip";
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
    }

    private void registerExamples() {
        getServer().getPluginManager().registerEvents(new ActionBarExample(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveChatExample(), this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @NotNull
    public Config getPluginConfig() {
        return config;
    }
}
