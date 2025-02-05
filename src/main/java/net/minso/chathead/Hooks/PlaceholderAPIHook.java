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

        // %chathead% or %chathead_self% - Returns the head of the player who requested the placeholder.
        if (params.isEmpty() || params.equalsIgnoreCase("self")) {
            if (offlinePlayer == null) return "No player found!";
            Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
            if (player == null) return "You must be online!";

            return api.getHeadAsString(player, true, ChatHeadAPI.defaultSource);
        }

        // %chathead_other_<player>% - Returns the head of the specified player.
        if (params.startsWith("other:")) {
            String targetName = params.substring(6);
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);
            if (targetPlayer == null) return "Player not found!";

            return api.getHeadAsString(targetPlayer, true, ChatHeadAPI.defaultSource);
        }

        return "Invalid placeholder!";
    }


    public static void registerHook(Main plugin) {
        new PlaceholderAPIHook(plugin).register();
    }
}
