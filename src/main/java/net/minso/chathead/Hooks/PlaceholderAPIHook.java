package net.minso.chathead.Hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import net.minso.chathead.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.Buffer;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final JavaPlugin plugin;

    public PlaceholderAPIHook(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "chathead";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }


    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        ChatHeadAPI api = ChatHeadAPI.getInstance();
        if (params.equalsIgnoreCase("me")) {
            if (offlinePlayer == null || !offlinePlayer.isOnline())
                return "This placeholder has to be used as an online player!";
            Player player = offlinePlayer.getPlayer();
            assert player != null;

            return api.getHeadAsString(player, true, ChatHeadAPI.defaultSource);
        }

        return api.getHeadAsString(Bukkit.getOfflinePlayer(params) , true, ChatHeadAPI.defaultSource);
    }


    public static void registerHook(Main plugin) {
        new PlaceholderAPIHook(plugin).register();
    }
}
