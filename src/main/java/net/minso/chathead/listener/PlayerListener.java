package net.minso.chathead.listener;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import net.minso.chathead.API.impl.MojangSource;
import net.minso.chathead.Main;
import org.bukkit.Bukkit;
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

        if (!Bukkit.getServer().getOnlineMode() && plugin.getPluginConfig().getServerOnlineMode())
            Bukkit.getLogger().warning(" CHATHEAD - Server is currently in OFFLINE MODE! Change online-mode in the config!");

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (plugin.getPluginConfig().getAutoDownloadPackEnabled()
                && plugin.getServer().getResourcePack().isEmpty())
            event.getPlayer().setResourcePack(Main.RESOURCE_PACK);

        if (plugin.getPluginConfig().getJoinMessagesEnabled()) {
            String joinMessage = event.getJoinMessage();
            event.setJoinMessage(null);

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                broadcast(joinMessage, event.getPlayer());
            }, 20 * plugin.getPluginConfig().getJoinMessagesDelaySeconds()); //Send message 3seconds later to fix issue with texture messing up while loading the texture pack.
        }
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
        SkinSource skinSource = plugin.getPluginConfig().getServerOnlineMode()
                ? ChatHeadAPI.defaultSource
                : new MojangSource(false);

        ChatHeadAPI api = ChatHeadAPI.getInstance();
        BaseComponent[] head = api.getHead(player, plugin.getPluginConfig().getSkinOverlayEnabled(), skinSource);

        ComponentBuilder builder = new ComponentBuilder();

        if (head != null && head.length > 0) {
            builder.append(head);
            builder.append(" ");
        }

        builder.append(message, ComponentBuilder.FormatRetention.NONE);
        BaseComponent[] msg = builder.create();

        return TextComponent.toLegacyText(msg);
    }

    private void broadcast(String msg, Player player) {
        String message = insertPlayerHead(msg, player);
        for (Player p : plugin.getServer().getOnlinePlayers())
            p.sendMessage(message);

        plugin.getServer().getConsoleSender().sendMessage(msg);
    }
}
