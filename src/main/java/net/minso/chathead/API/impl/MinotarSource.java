package net.minso.chathead.API.impl;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minso.chathead.API.SkinSource;
import net.minso.chathead.API.SkinSourceEnum;
import org.bukkit.OfflinePlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * SkinSource implementation to retrieve heads from Minotar.
 */
public class MinotarSource extends SkinSource {

    public MinotarSource(boolean useUUIDWhenRetrieve) {
        super(SkinSourceEnum.MCHEADS, true, useUUIDWhenRetrieve);
    }

    public MinotarSource() {
        super(SkinSourceEnum.MCHEADS, true);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public BaseComponent[] getHead(OfflinePlayer player, boolean overlay) {

        String[] colors = new String[64]; // Initialize an array to store the pixel colors
        try {
            String baseUrl = "https://minotar.net/"; // The base URL for Minotar
            String endpoint = overlay ? "helm" : "avatar"; // Determine the endpoint based on whether overlay is requested
            String uuidOrUsername = useUUIDWhenRetrieve() ? player.getUniqueId().toString().replace("-", "").trim() : player.getName(); // Trims the UUID, removing dashes
            String imageUrl = baseUrl + endpoint + "/" + uuidOrUsername + "/8.png"; // Construct the URL for fetching the players image from Minotar

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
        return toBaseComponent(colors);

    }


}

