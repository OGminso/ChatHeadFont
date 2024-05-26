package net.minso.chathead.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minso.chathead.API.ChatHeadAPI;
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
        if (plugin.getPluginConfig().getJoinMessagesEnabled()) {
            broadcast(event.getJoinMessage(), event.getPlayer());
            event.setJoinMessage(null);
        }

        if (plugin.getPluginConfig().getAutoDownloadPackEnabled()
                && plugin.getServer().getResourcePack().isEmpty())
            event.getPlayer().setResourcePack(Main.RESOURCE_PACK);
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.getPluginConfig().getLeaveMessagesEnabled()) return;

        broadcast(event.getQuitMessage(), event.getPlayer());
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getPluginConfig().getChatMessagesEnabled()) return;

        String msg = String.format(event.getFormat(), event.getPlayer().getName(), event.getMessage());
        event.setCancelled(true);
        broadcast(msg, event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getPluginConfig().getDeathMessagesEnabled()) return;

        event.setDeathMessage(insertPlayerHead(event.getDeathMessage(), event.getEntity()));
    }

    private String insertPlayerHead(String message, Player player) {
        ChatHeadAPI api = ChatHeadAPI.getInstance();
        BaseComponent[] head = api.getHead(player, true, ChatHeadAPI.defaultSource);
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
