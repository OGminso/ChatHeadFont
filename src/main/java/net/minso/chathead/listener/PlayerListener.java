package net.minso.chathead.listener;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(insertPlayerHead(event.getJoinMessage(), event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(insertPlayerHead(event.getQuitMessage(), event.getPlayer()));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(insertPlayerHead(event.getFormat(), event.getPlayer()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(insertPlayerHead(event.getDeathMessage(), event.getEntity()));
    }

    private String insertPlayerHead(String message, Player player) {
        ChatHeadAPI api = ChatHeadAPI.getInstance();
        BaseComponent[] head = api.getHead(player, true, SkinSource.CRAFATAR);
        BaseComponent[] msg = new ComponentBuilder()
                .append(head)
                .append(" ")
                .append(message, ComponentBuilder.FormatRetention.NONE)
                .create();

        return TextComponent.toLegacyText(msg);
    }
}
