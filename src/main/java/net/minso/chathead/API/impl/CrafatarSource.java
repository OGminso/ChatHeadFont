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
 * SkinSource implementation to retrieve heads from Crafatar.
 */
public class CrafatarSource extends SkinSource {

    public CrafatarSource(boolean useUUIDWhenRetrieve) {
        super(SkinSourceEnum.MCHEADS, false, useUUIDWhenRetrieve);
    }

    public CrafatarSource() {
        super(SkinSourceEnum.MCHEADS, false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public BaseComponent[] getHead(OfflinePlayer player, boolean overlay) {

        if (!hasUsernameSupport() && !useUUIDWhenRetrieve()) {
            throw new UnsupportedOperationException("CrafatarSource does not support username to retrieve player heads");
        }

        String[] colors = new String[64]; // Initialize an array to store the pixel colors
        try {
            String url = "https://crafatar.com/avatars/" + player.getUniqueId() + "?size=8"; // URL for fetching the players image from Crafatar
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
        return toBaseComponent(colors); // Return the array containing the pixel colors

    }

}
