package net.minso.chathead.Examples;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minso.chathead.API.ChatHeadAPI;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BossbarExample implements Listener {

    private BossBar bossBar;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Retrieve the ChatHeadAPI instance
        ChatHeadAPI chatHeadAPI = ChatHeadAPI.getInstance();

        // Get the BaseComponent array representing the players head with overlay from the specified skin source
        BaseComponent[] components = chatHeadAPI.getHead(player, true, ChatHeadAPI.defaultSource);

        //Convert the BaseComponent array to a legacy format.
        String title = TextComponent.toLegacyText(components);

        // Create a new BossBar with the players head as the title
        bossBar = Bukkit.createBossBar(
                title.toString(),
                BarColor.BLUE,
                BarStyle.SOLID
        );

        // Add the player to the BossBar
        bossBar.addPlayer(player);

    }

}
