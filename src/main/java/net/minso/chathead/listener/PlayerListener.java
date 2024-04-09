package net.minso.chathead.listener;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import net.minso.chathead.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        broadcastToPlayers(insertPlayerHead(event.getJoinMessage(), event.getPlayer()));
        broadcast(event.getJoinMessage(), event.getPlayer());
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        broadcast(event.getQuitMessage(), event.getPlayer());
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String msg = String.format(event.getFormat(), event.getPlayer().getName(), event.getMessage());
        event.setCancelled(true);
        broadcast(msg, event.getPlayer());
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

    private void broadcast(String msg, Player player) {
        String message = insertPlayerHead(msg, player);
        for (Player p : plugin.getServer().getOnlinePlayers())
            p.sendMessage(message);

        plugin.getServer().getConsoleSender().sendMessage(msg);
    }
}
