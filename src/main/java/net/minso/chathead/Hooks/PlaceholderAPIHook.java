package net.minso.chathead.Hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.chat.TextComponent;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "chathead";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Minso";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.2";
    }


    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            if (params.equalsIgnoreCase("player")) {
                return TextComponent.toLegacyText(ChatHeadAPI.getInstance().getHead(player.getUniqueId(), true, SkinSource.CRAFATAR));
            }
        }

        return null;
    }

    public static void registerHook() {
        new PlaceholderAPIHook().register();
    }
}
