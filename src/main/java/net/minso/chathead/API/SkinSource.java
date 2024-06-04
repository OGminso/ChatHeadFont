package net.minso.chathead.API;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * Abstract class to manage SkinSources
 */
public abstract class SkinSource {

    private final SkinSourceEnum skinSource;

    private final boolean hasUsernameSupport;

    private final boolean useUUIDWhenRetrieve;

    /**
     * Create a new SkinSource.
     *
     * @param skinSource The SkinSource.
     * @param hasUsernameSupport If it has the support of requesting the player's head by name.
     * @param useUUIDWhenRetrieve If it uses the UUID to request the head.
     */
    public SkinSource(SkinSourceEnum skinSource, boolean hasUsernameSupport, boolean useUUIDWhenRetrieve) {
        this.skinSource = skinSource;
        this.hasUsernameSupport = hasUsernameSupport;
        this.useUUIDWhenRetrieve = useUUIDWhenRetrieve;
    }


    /**
     * Create a new SkinSource.
     * Put {@code useUUIDWhenRetrieve} to {@code true}.
     *
     * @param skinSource The SkinSource.
     * @param hasUsernameSupport If it has the support of requesting the player's head by name.
     */
    public SkinSource(SkinSourceEnum skinSource, boolean hasUsernameSupport) {
        this.skinSource = skinSource;
        this.hasUsernameSupport = hasUsernameSupport;
        this.useUUIDWhenRetrieve = true;
    }


    /**
     * Retrieves a 8x8 grid of pixels representing a players head, using the player's UUID and specified options.
     *
     * @param player     The Player object representing the player whose head is to be retrieved.
     * @param overlay    A boolean value indicating whether to apply overlay on the players head.
     *                   Supported sources include MOJANG, MINOTAR, and CRAFATAR.
     * @return           An array of BaseComponents representing the player's head.
     *                   Each BaseComponent represents a single pixel, forming a 8x8 grid of pixels.
     */
    abstract public BaseComponent[] getHead(OfflinePlayer player, boolean overlay);


    /**
     * After obtaining the 8x8 grid in hex form, transform it into BaseComponent[].
     *
     * @param hexColors The 8x8 grid in hex form.
     * @return The 8x8 grid in BaseComponent[].
     */
    public BaseComponent[] toBaseComponent(String[] hexColors) {
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
     * Retrieves the pixel colors from the skin image of a Minecraft player.
     * The function fetches the player's skin image from the provided URL and extracts
     * the color information of each pixel from the face region of the skin.
     * Optionally, it can apply an overlay effect by extracting color information
     * from a specified overlay region of the skin image.
     *
     * @param playerSkinUrl The URL of the Minecraft player's skin image.
     * @param overlay       A boolean value indicating whether to apply an overlay effect.
     *                      If set to true, an overlay effect will be applied; otherwise, only the face region colors will be extracted.
     * @return An array of strings representing the hexadecimal color codes of pixels extracted from the player's skin image.
     * Each string in the array represents the color of a single pixel.
     * The array has a fixed length of 64 elements, corresponding to an 8x8 grid of pixels.
     * If any error occurs during the retrieval or processing of the skin image, an empty array is returned.
     */
    public String[] getPixelColorsFromSkin(String playerSkinUrl, boolean overlay) {
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

    public SkinSourceEnum getSkinSource() {
        return skinSource;
    }

    public boolean hasUsernameSupport() {
        return hasUsernameSupport;
    }

    public boolean useUUIDWhenRetrieve() {
        return useUUIDWhenRetrieve;
    }
}
