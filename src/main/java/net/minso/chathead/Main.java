package net.minso.chathead;

import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.Examples.ActionBarExample;
import net.minso.chathead.Examples.JoinLeaveChatExample;
import net.minso.chathead.Hooks.PlaceholderAPIHook;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        ChatHeadAPI.initialize(this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            PlaceholderAPIHook.registerHook(this);
            getLogger().info("Hooked into PlaceholderAPI!");
        }

        //registerExamples();
    }

    private void registerExamples() {
        getServer().getPluginManager().registerEvents(new ActionBarExample(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveChatExample(), this);
    }



}
