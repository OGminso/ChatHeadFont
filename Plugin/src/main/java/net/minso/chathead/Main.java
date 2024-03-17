package net.minso.chathead;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player player = event.getPlayer();

        TextComponent textComponent = new TextComponent();
        textComponent.setText(" ");
        textComponent.setFont("minecraft:default");

        BaseComponent[] head1 = getHead(UUID.fromString("d30e61cb-f6d6-4941-b30c-b2c6adce9254"));
        BaseComponent[] head2 = getHead(UUID.fromString("c2013a02-a45b-411c-b79a-006ee3ec8295"));
        BaseComponent[] head3 = getHead(UUID.fromString("f0453a4c-abd1-4fc9-9f1d-6bab7f685096"));
        BaseComponent[] head4 = getHead(UUID.fromString("84e96f90-203e-449e-9af7-95a383d6ff1a"));
        BaseComponent[] head5 = getHead(player);

        BaseComponent[] combinedHeads = new ComponentBuilder()
                .append(head1)
                .append(textComponent)
                .append(head2)
                .append(textComponent)
                .append(head3)
                .append(textComponent)
                .append(head4)
                .append(textComponent)
                .append(head5)
                .create();

        player.spigot().sendMessage(combinedHeads);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, combinedHeads);


    }

    public BaseComponent[] getHead(Player player) {
        return getHead(player.getUniqueId());
    }

    public BaseComponent[] getHead(UUID uuid) {
        String[] hexColors = getPixelColors(getPlayerSkinURL(uuid));

        if (hexColors == null || hexColors.length < 64) {
            throw new IllegalArgumentException("Hex colors array must have at least 64 elements.");
        }

        TextComponent[][] components = new TextComponent[8][8];

        for (int i = 0; i < 64; i++) {
            int row = i / 8;
            int col = i % 8;
            char unicodeChar = (char) ('\uF000' + (i % 8) + 1);
            char spaceChar;
            TextComponent component = new TextComponent();
            if (i == 7 || i == 15 || i == 23 || i == 31 || i == 39 || i == 47 || i == 55) {
                component.setText(Character.toString(unicodeChar) + Character.toString('\uF101'));
            } else if (i == 63) {
                component.setText(Character.toString(unicodeChar));
            } else {
                component.setText(Character.toString(unicodeChar) + Character.toString('\uF102'));
            }

            component.setColor(ChatColor.of(hexColors[i]));
            component.setFont("minecraft:playerhead");
            components[row][col] = component;
        }

        BaseComponent[] baseComponents = new ComponentBuilder()
                .append(Arrays.stream(components)
                        .flatMap(Arrays::stream)
                        .toArray(TextComponent[]::new))
                .create();

        return baseComponents;
    }

    private String[] getPixelColors(String playerSkinUrl) {
        String[] colors = new String[64];
        try {
            BufferedImage skinImage = ImageIO.read(new URL(playerSkinUrl));

            int faceStartX = 8;
            int faceStartY = 8;
            int faceWidth = 8;
            int faceHeight = 8;

            BufferedImage faceImage = skinImage.getSubimage(faceStartX, faceStartY, faceWidth, faceHeight);

            int index = 0;
            for (int x = 0; x < faceHeight; x++) {
                for (int y = 0; y < faceWidth; y++) {
                    int rgb = faceImage.getRGB(x, y);
                    String hexColor = String.format("#%06X", (rgb & 0xFFFFFF));
                    colors[index++] = hexColor;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colors;
    }


    private String getPlayerSkinURL(UUID uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray propertiesArray = jsonObject.getJSONArray("properties");
            for (int i = 0; i < propertiesArray.length(); i++) {
                JSONObject property = propertiesArray.getJSONObject(i);
                if (property.getString("name").equals("textures")) {
                    String value = property.getString("value");
                    byte[] decodedBytes = Base64.getDecoder().decode(value);
                    String decodedValue = new String(decodedBytes);
                    JSONObject textureJson = new JSONObject(decodedValue);
                    return textureJson.getJSONObject("textures").getJSONObject("SKIN").getString("url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unable to retrieve player skin URL.";
    }



}
