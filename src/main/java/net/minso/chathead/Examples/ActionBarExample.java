package net.minso.chathead.Examples;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * An example demonstrating the usage of ChatHeadAPI to display a players head
 * in the action bar when they join the server.
 */
public class ActionBarExample implements Listener {

    /**
     * Handles the PlayerJoinEvent to display a players head in the action bar upon joining the server.
     *
     * @param event The PlayerJoinEvent triggered when a player joins the server.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Retrieve the ChatHeadAPI instance
        ChatHeadAPI chatHeadAPI = ChatHeadAPI.getInstance();

        // Get the BaseComponent array representing the players head with overlay from the specified skin source
        BaseComponent[] component = chatHeadAPI.getHead(player, true, ChatHeadAPI.defaultSource);

        // Send the players head as an action bar message
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR ,component);

    }

}
