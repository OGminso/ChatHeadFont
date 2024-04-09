package net.minso.chathead.listener;

import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ChatHeadAPI api = ChatHeadAPI.getInstance();

        String joinMsg =
                api.getHeadAsString(player, true, SkinSource.CRAFATAR) + " " + event.getJoinMessage();

        event.setJoinMessage(joinMsg);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ChatHeadAPI api = ChatHeadAPI.getInstance();

        String quitMsg =
                api.getHeadAsString(player, true, SkinSource.CRAFATAR) + " " + event.getQuitMessage();

        event.setQuitMessage(quitMsg);
    }
}
