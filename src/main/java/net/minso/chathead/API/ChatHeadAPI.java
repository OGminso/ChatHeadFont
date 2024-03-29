package net.minso.chathead.API;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
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


/**
 * A class for retrieving player head representations as BaseComponents.
 * The head representation is generated based on the players UUID and skin source.
 * @author Minso
 */
public class ChatHeadAPI {

    private static ChatHeadAPI instance;
    private final JavaPlugin plugin;

    /**
     * Constructs a new ChatHeadAPI instance.
     *
     * @param plugin The JavaPlugin instance associated with the ChatHeadAPI.
     */
    public ChatHeadAPI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Retrieves the singleton instance of the ChatHeadAPI.
     *
     * @return The singleton instance of ChatHeadAPI.
     * @throws IllegalArgumentException If ChatHeadAPI has not been initialized.
     */
    public static ChatHeadAPI getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("ChatHeadAPI has not been initialized.");
        }
        return instance;
    }

    /**
     * Initializes the ChatHeadAPI with the provided JavaPlugin instance.
     *
     * @param plugin The JavaPlugin instance to associate with the ChatHeadAPI.
     * @throws IllegalStateException If ChatHeadAPI has already been initialized.
     */
    public static void initialize(JavaPlugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("PlayerHeadAPI has already been initialized.");
        }
        instance = new ChatHeadAPI(plugin);
    }


    /**
     * Retrieves a 8x8 grid of pixels representing a players head, using the player's UUID and specified options.
     *
     * @param player     The Player object representing the player whose head is to be retrieved.
     * @param overlay    A boolean value indicating whether to apply overlay on the players head.
     * @param skinSource An enum specifying the source from which to retrieve the player's skin.
     *                   Supported sources include MOJANG, MINOTAR, and CRAFATAR.
     * @return           An array of BaseComponents representing the player's head.
     *                   Each BaseComponent represents a single pixel, forming a 8x8 grid of pixels.
     */
    public BaseComponent[] getHead(Player player, boolean overlay, SkinSource skinSource) {
        // Delegate the retrieval of the players head to the overloaded getHead method using the player's UUID
        return getHead(player.getUniqueId(), overlay, skinSource);
    }


    /**
     * Creates a 8x8 grid of pixels representing a Minecraft player's head.
     * Each pixel in the grid is represented by a TextComponent with a specified hexadecimal color.
     *
     * @param uuid       The UUID of the player whose head is to be retrieved & created.
     * @param overlay    A boolean value indicating whether to apply overlay on the players head.
     * @param skinSource An enum specifying the source from which to retrieve the player's skin.
     *                   Supported sources include MOJANG, MINOTAR, and CRAFATAR.
     * @return           An array of BaseComponents representing the player's head.
     *                   Each BaseComponent represents a single pixel, forming a 8x8 grid of pixels.
     */
    public BaseComponent[] getHead(UUID uuid, boolean overlay, SkinSource skinSource) {
        // Retrieve the hexadecimal colors array representing the player's skin based on the specified skin source
        String[] hexColors = null;
        switch (skinSource) {
            case MOJANG:
                hexColors = getPixelColorsFromSkin(getPlayerSkinFromMojang(uuid), overlay);
                break;
            case MINOTAR:
                hexColors = getPixelColorsFromMinotar(uuid, overlay);
                break;
            case CRAFATAR:
                hexColors = getPixelColorsFromCrafatar(uuid, overlay);
                break;
        }

        // Check if the retrieved colors array is valid (has at least 64 elements)
        if (hexColors == null || hexColors.length < 64) {
            throw new IllegalArgumentException("Hex colors must have at least 64 elements.");
        } //TODO add error handling

        // Initialize a 2D array to store TextComponents representing each pixel of the players head
        TextComponent[][] components = new TextComponent[8][8];

        for (int i = 0; i < 64; i++) {
            int row = i / 8;
            int col = i % 8;
            char unicodeChar = (char) ('\uF000' + (i % 8) + 1);
            TextComponent component = new TextComponent();

            // Determine the character and styling based on the position of the pixel within the 8x8 grid
            if (i == 7 || i == 15 || i == 23 || i == 31 || i == 39 || i == 47 || i == 55) {
                component.setText(unicodeChar + Character.toString('\uF101'));
            } else if (i == 63) {
                component.setText(Character.toString(unicodeChar));
            } else {
                component.setText(unicodeChar + Character.toString('\uF102'));
            }

            // Set the color of the TextComponent based on the corresponding hexadecimal color
            component.setColor(ChatColor.of(hexColors[i]));
            component.setFont("minecraft:playerhead"); //Sets the components font to the designated font containing the pixels.
            components[row][col] = component;
        }

        // Create a default TextComponent with no text and the default font.
        TextComponent defaultFont = new TextComponent();
        defaultFont.setText("");
        defaultFont.setFont("minecraft:default");

        // Construct the array of BaseComponents representing the player's head by appending the TextComponents
        BaseComponent[] baseComponents = new ComponentBuilder()
                .append(Arrays.stream(components)
                        .flatMap(Arrays::stream)
                        .toArray(TextComponent[]::new))
                .append(defaultFont)
                .create();

        return baseComponents; // Return the array of BaseComponents representing the players head
    }


    /**
     * Retrieves the pixel colors from the avatar image of a player hosted on Crafatar.
     * The function fetches the avatar image from Crafatar using the provided player's UUID and extracts
     * the color information of each pixel from the image.
     *
     * @param uuid     The UUID of the player whose avatar is to be retrieved.
     * @param overlay  A boolean indicating whether to apply the skin overlay on the avatar image.
     * @return         An array of Strings containing hexadecimal colors of pixels extracted from the player's avatar image.
     *                 Each string in the array represents the color of a single pixel in the avatar image.
     *                 The array has a fixed length of 64 elements, corresponding to a 8x8 grid of pixels.
     */
    private String[] getPixelColorsFromCrafatar(UUID uuid, boolean overlay) {
        String[] colors = new String[64]; // Initialize an array to store the pixel colors
        try {
            String url = "https://crafatar.com/avatars/" + uuid + "?size=8"; // URL for fetching the players image from Crafatar
            if (overlay) url += "&overlay";  // Append overlay parameter to the URL if overlay effects are requested
            BufferedImage skinImage = ImageIO.read(new URL(url)); // Read the avatar image from the constructed URL
            int faceWidth = 8, faceHeight = 8; // Define dimensions of the face (8x8)

            int index = 0;
            // Iterate through each pixel of the avatar image and extract its color
            for (int x = 0; x < faceHeight; x++) {
                for (int y = 0; y < faceWidth; y++) {
                    // Convert RGB value to hexadecimal string representation and store it in the array
                    colors[index++] = String.format("#%06X", (skinImage.getRGB(x, y) & 0xFFFFFF));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colors; // Return the array containing the pixel colors
    }

    /**
     * Retrieves the pixel colors from the avatar image of a player hosted on Minotar.
     * The function fetches the avatar image from Minotar using the provided player's UUID and extracts
     * the color information of each pixel from the image.
     *
     * @param uuid     The UUID of the player whose avatar is to be retrieved.
     * @param overlay  A boolean indicating whether to apply the skin overlay on the avatar image.
     * @return         An array of Strings containing hexadecimal colors of pixels extracted from the player's avatar image.
     *                 Each string in the array represents the color of a single pixel in the avatar image.
     *                 The array has a fixed length of 64 elements, corresponding to a 8x8 grid of pixels.
     */
    private String[] getPixelColorsFromMinotar(UUID uuid, boolean overlay) {
        String[] colors = new String[64]; // Initialize an array to store the pixel colors
        try {
            String baseUrl = "https://minotar.net/"; // The base URL for Minotar
            String endpoint = overlay ? "helm" : "avatar"; // Determine the endpoint based on whether overlay is requested
            String trimmedUUID = uuid.toString().replace("-", "").trim(); // Trims the UUID, removing dashes
            String imageUrl = baseUrl + endpoint + "/" + trimmedUUID + "/8.png"; // Construct the URL for fetching the players image from Minotar

            BufferedImage skinImage = ImageIO.read(new URL(imageUrl)); // Read the avatar image from the constructed URL
            int faceWidth = 8, faceHeight = 8; // Define dimensions of the face (8x8)

            // Iterate through each pixel of the avatar image and extract its color
            int index = 0;
            for (int x = 0; x < faceHeight; x++) {
                for (int y = 0; y < faceWidth; y++) {
                    // Convert RGB value to hexadecimal string representation and store it in the array
                    colors[index++] = String.format("#%06X", (skinImage.getRGB(x, y) & 0xFFFFFF));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colors;
    }

    /**
     * Retrieves the pixel colors from the skin image of a Minecraft player.
     * The function fetches the player's skin image from the provided URL and extracts
     * the color information of each pixel from the face region of the skin.
     * Optionally, it can apply an overlay effect by extracting color information
     * from a specified overlay region of the skin image.
     *
     * @param playerSkinUrl The URL of the Minecraft player's skin image.
     * @param overlay       A boolean value indicating whether to apply an overlay effect.
     *                      If set to true, an overlay effect will be applied; otherwise, only the face region colors will be extracted.
     * @return              An array of strings representing the hexadecimal color codes of pixels extracted from the player's skin image.
     *                      Each string in the array represents the color of a single pixel.
     *                      The array has a fixed length of 64 elements, corresponding to an 8x8 grid of pixels.
     *                      If any error occurs during the retrieval or processing of the skin image, an empty array is returned.
     */
    private String[] getPixelColorsFromSkin(String playerSkinUrl, boolean overlay) {
        String[] colors = new String[64];
        try {
            BufferedImage skinImage = ImageIO.read(new URL(playerSkinUrl));

            int faceStartX = 8, faceStartY = 8;
            int faceWidth = 8, faceHeight = 8;

            int overlayStartX = 40;
            int overlayStartY = 8;

            BufferedImage faceImage = skinImage.getSubimage(faceStartX, faceStartY, faceWidth, faceHeight);
            BufferedImage overlayImage = skinImage.getSubimage(overlayStartX, overlayStartY, faceWidth, faceHeight);

            int index = 0;
            for (int x = 0; x < faceHeight; x++) {
                for (int y = 0; y < faceWidth; y++) {
                    int rgbFace = faceImage.getRGB(x, y);
                    int rgbOverlay = overlayImage.getRGB(x, y);

                    // Check if the overlay pixel is not transparent
                    if ((rgbOverlay >> 24) != 0x00 && overlay) {
                        colors[index++] = String.format("#%06X", (rgbOverlay & 0xFFFFFF)); // Use overlay color
                    } else {
                        colors[index++] = String.format("#%06X", (rgbFace & 0xFFFFFF)); // Use face color
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colors; // Return the array containing the pixel colors
    }


    /**
     * Retrieves the URL of the players skin hosted on Mojangs session server.
     * The function sends a GET request to Mojangs session server with the provided players UUID,
     * parses the JSON response to extract the skin URL, and returns it.
     *
     * @param uuid The UUID of the player whose skin URL is to be retrieved.
     * @return A string representing the URL of the player's skin.
     */
    private String getPlayerSkinFromMojang(UUID uuid) {
        try {
            // Construct the URL for fetching player's profile information from Mojang's session server
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
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
