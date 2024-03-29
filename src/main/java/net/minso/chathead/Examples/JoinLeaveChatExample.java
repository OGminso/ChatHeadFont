package net.minso.chathead.Examples;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * An example demonstrating the usage of ChatHeadAPI to display a players head
 * in the chat when they join and leave the server.
 */
public class JoinLeaveChatExample implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        Player player = event.getPlayer();

        // Retrieve the ChatHeadAPI instance
        ChatHeadAPI chatHeadAPI = ChatHeadAPI.getInstance();

        // Get the BaseComponent array representing the players head with overlay from the specified skin source
        BaseComponent[] head = chatHeadAPI.getHead(player, true, SkinSource.CRAFATAR);
        TextComponent msg = new TextComponent(ChatColor.YELLOW + " " + player.getName() + " joined the game");
        BaseComponent[] joinMsg = new ComponentBuilder().append(head).append(msg).create();

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.spigot().sendMessage(joinMsg);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");

        Player player = event.getPlayer();

        // Retrieve the ChatHeadAPI instance
        ChatHeadAPI chatHeadAPI = ChatHeadAPI.getInstance();

        // Get the BaseComponent array representing the players head with overlay from the specified skin source
        BaseComponent[] head = chatHeadAPI.getHead(player, true, SkinSource.CRAFATAR);
        TextComponent msg = new TextComponent(ChatColor.YELLOW + " " + player.getName() + " left the game");
        BaseComponent[] joinMsg = new ComponentBuilder().append(head).append(msg).create();

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.spigot().sendMessage(joinMsg);
        }

    }


}
