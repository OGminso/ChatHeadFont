package net.minso.chathead.API.impl;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minso.chathead.API.SkinSource;
import net.minso.chathead.API.SkinSourceEnum;
import org.bukkit.OfflinePlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * SkinSource implementation to retrieve heads from Mojang.
 */
public class MojangSource extends SkinSource {


    public MojangSource(boolean useUUIDWhenRetrieve) {
        super(SkinSourceEnum.MOJANG, true, useUUIDWhenRetrieve);
    }

    public MojangSource() {
        super(SkinSourceEnum.MOJANG, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseComponent[] getHead(OfflinePlayer player, boolean overlay) {

        if (useUUIDWhenRetrieve()) {
            return toBaseComponent(getPixelColorsFromSkin(getPlayerSkinFromMojang(player.getUniqueId().toString()), overlay));
        } else {
            return toBaseComponent(getPixelColorsFromSkin(getPlayerSkinFromMojang(getUUIDFromName(player)), overlay));
        }

    }

    /**
     * Get the id by knowing the player's name.
     *
     * @param offlinePlayer The player.
     * @return the id by knowing the player's name.
     */
    public String getUUIDFromName(OfflinePlayer offlinePlayer) {
        try {
            // Construct the URL for fetching player's profile information from Mojang's session server
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + offlinePlayer.getName());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                // Read the response from the connection and append it to the StringBuilder
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close(); // Close the reader
                // Parse the JSON response
                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                return jsonObject.getString("id");

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "";
        }

    }


    /**
     * Retrieves the URL of the players skin hosted on Mojangs session server.
     * The function sends a GET request to Mojangs session server with the provided players UUID,
     * parses the JSON response to extract the skin URL, and returns it.
     *
     * @param uuid The UUID of the player whose skin URL is to be retrieved.
     * @return A string representing the URL of the player's skin.
     */
    private String getPlayerSkinFromMojang(String uuid) {
        try {
            // Construct the URL for fetching player's profile information from Mojang's session server
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                // Read the response from the connection and append it to the StringBuilder
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close(); // Close the reader
                // Parse the JSON response
                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray propertiesArray = jsonObject.getJSONArray("properties");

                // Iterate through the properties array to find the textures property
                for (int i = 0; i < propertiesArray.length(); i++) {
                    JSONObject property = propertiesArray.getJSONObject(i);
                    if (property.getString("name").equals("textures")) {
                        String value = property.getString("value");
                        // Decode the Base64 encoded value
                        byte[] decodedBytes = Base64.getDecoder().decode(value);
                        String decodedValue = new String(decodedBytes);
                        JSONObject textureJson = new JSONObject(decodedValue);
                        // Extract and return the URL of the player's skin
                        return textureJson.getJSONObject("textures").getJSONObject("SKIN").getString("url");
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "Unable to retrieve player skin URL."; //TODO Add error handling
    }


}
