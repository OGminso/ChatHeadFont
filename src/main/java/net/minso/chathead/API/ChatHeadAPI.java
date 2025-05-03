package net.minso.chathead.API;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minso.chathead.API.impl.CrafatarSource;
import net.minso.chathead.API.impl.McHeadsSource;
import net.minso.chathead.API.impl.MinotarSource;
import net.minso.chathead.API.impl.MojangSource;
import net.minso.chathead.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * The {@code ChatHeadAPI} class provides methods to retrieve a Minecraft player's head representation
 * as an array of {@link BaseComponent} objects forming an 8x8 grid of pixels. Each pixel is represented
 * by a {@link BaseComponent} that contains a hexadecimal color code.
 * <p>
 * The default skin source is determined from the plugin's configuration using the key "skin-source"
 * (case-insensitive), which defaults to "MOJANG" if not specified.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     ChatHeadAPI.initialize(plugin);
 *     BaseComponent[] headComponents = ChatHeadAPI.getInstance().getHead(playerUUID);
 * </pre>
 * </p>
 *
 * @author Minso
 */
public class ChatHeadAPI {

    /**
     * The default {@link SkinSource} used by this API, as determined during initialization.
     */
    public static SkinSource defaultSource;

    private static ChatHeadAPI instance;

    private final Main plugin;
    private final HeadCache headCache;

    /**
     * Constructs a new {@code ChatHeadAPI} instance.
     *
     * @param plugin the {@link Main} instance associated with this API.
     */
    public ChatHeadAPI(Main plugin) {
        this.plugin = plugin;
        this.headCache = new HeadCache(plugin);
    }

    /**
     * Retrieves the singleton instance of the {@code ChatHeadAPI}.
     *
     * @return the singleton instance of {@code ChatHeadAPI}.
     * @throws IllegalArgumentException if {@code ChatHeadAPI} has not been initialized via {@link #initialize(Main)}.
     */
    public static ChatHeadAPI getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("ChatHeadAPI has not been initialized.");
        }
        return instance;
    }

    /**
     * Initializes the {@code ChatHeadAPI} with the provided {@link Main} instance.
     * <p>
     * This method reads the "skin-source" configuration from the plugin's configuration file,
     * sets the default skin source accordingly, and creates the singleton instance. If the API is
     * already initialized, an {@link IllegalStateException} is thrown.
     * </p>
     *
     * @param plugin the {@link Main} instance to associate with the {@code ChatHeadAPI}.
     * @throws IllegalStateException if {@code ChatHeadAPI} has already been initialized.
     */
    public static void initialize(Main plugin) {
        if (instance != null) {
            throw new IllegalStateException("PlayerHeadAPI has already been initialized.");
        }

        String skinSourceConfig = plugin.getConfig().getString("skin-source", "MOJANG");
        defaultSource = switch (skinSourceConfig.toUpperCase()) {
            case "CRAFATAR" -> new CrafatarSource(plugin.isOfflineModeEnabled());
            case "MINOTAR" -> new MinotarSource(plugin.isOfflineModeEnabled());
            case "MCHEADS" -> new McHeadsSource(plugin.isOfflineModeEnabled());
            default -> new MojangSource();
        };

        instance = new ChatHeadAPI(plugin);
    }

    /**
     * Retrieves an 8x8 pixel head representation for the player identified by the specified UUID.
     * <p>
     * The resulting array of {@link BaseComponent} objects represents the player's head,
     * with each component corresponding to a pixel's hexadecimal color value. This method applies
     * the skin overlay by default and uses the default skin source.
     * </p>
     *
     * @param uuid the UUID of the player whose head is to be retrieved.
     * @return an array of {@link BaseComponent} objects representing the player's head.
     */
    public BaseComponent[] getHead(UUID uuid) {
        return headCache.getCachedHead(uuid, true, defaultSource);
    }

    /**
     * Retrieves an 8x8 pixel head representation for the player identified by the specified UUID,
     * allowing control over whether the skin overlay is applied.
     * <p>
     * This method uses the default skin source.
     * </p>
     *
     * @param uuid    the UUID of the player whose head is to be retrieved.
     * @param overlay {@code true} to apply the skin overlay; {@code false} otherwise.
     * @return an array of {@link BaseComponent} objects representing the player's head.
     */
    public BaseComponent[] getHead(UUID uuid, boolean overlay) {
        return headCache.getCachedHead(uuid, overlay, defaultSource);
    }

    /**
     * Retrieves an 8x8 pixel head representation for the player identified by the specified UUID,
     * allowing specification of both the overlay option and the skin source.
     *
     * @param uuid       the UUID of the player whose head is to be retrieved.
     * @param overlay    {@code true} to apply the skin overlay; {@code false} otherwise.
     * @param skinSource the {@link SkinSource} to use for retrieving the player's skin.
     * @return an array of {@link BaseComponent} objects representing the player's head.
     */
    public BaseComponent[] getHead(UUID uuid, boolean overlay, SkinSource skinSource) {
        return headCache.getCachedHead(uuid, overlay, skinSource);
    }

    /**
     * Retrieves an 8x8 pixel head representation for the specified {@link OfflinePlayer}.
     * <p>
     * This method applies the skin overlay by default and uses the default skin source.
     * </p>
     *
     * @param player the {@link OfflinePlayer} whose head is to be retrieved.
     * @return an array of {@link BaseComponent} objects representing the player's head.
     */
    public BaseComponent[] getHead(OfflinePlayer player) {
        return headCache.getCachedHead(player, true, defaultSource);
    }

    /**
     * Retrieves an 8x8 pixel head representation for the specified {@link OfflinePlayer},
     * allowing control over whether the skin overlay is applied.
     * <p>
     * This method uses the default skin source.
     * </p>
     *
     * @param player  the {@link OfflinePlayer} whose head is to be retrieved.
     * @param overlay {@code true} to apply the skin overlay; {@code false} otherwise.
     * @return an array of {@link BaseComponent} objects representing the player's head.
     */
    public BaseComponent[] getHead(OfflinePlayer player, boolean overlay) {
        return headCache.getCachedHead(player, overlay, defaultSource);
    }

    /**
     * Retrieves an 8x8 pixel head representation for the specified {@link OfflinePlayer},
     * allowing specification of both the overlay option and the skin source.
     *
     * @param player     the {@link OfflinePlayer} whose head is to be retrieved.
     * @param overlay    {@code true} to apply the skin overlay; {@code false} otherwise.
     * @param skinSource the {@link SkinSource} to use for retrieving the player's skin.
     * @return an array of {@link BaseComponent} objects representing the player's head.
     */
    public BaseComponent[] getHead(OfflinePlayer player, boolean overlay, SkinSource skinSource) {
        return headCache.getCachedHead(player, overlay, skinSource);
    }

    /**
     * Retrieves the player's head as a legacy-formatted string using the specified UUID.
     * <p>
     * This method converts the 8x8 grid of {@link BaseComponent} objects into a legacy text format
     * using {@link TextComponent#toLegacyText(BaseComponent[])}.
     * <strong>Note:</strong> Although this method accepts parameters for overlay and skin source,
     * it always applies the skin overlay and uses the default skin source.
     * </p>
     *
     * @param uuid       the UUID of the player whose head is to be retrieved.
     * @param overlay    an unused parameter; the head is always generated with the overlay applied.
     * @param skinSource an unused parameter; the default skin source is always used.
     * @return a legacy-formatted string representing the player's head.
     */
    public String getHeadAsString(UUID uuid, boolean overlay, SkinSource skinSource) {
        return getHeadAsString(Bukkit.getOfflinePlayer(uuid), true, defaultSource);
    }

    /**
     * Retrieves the player's head as a legacy-formatted string using the specified {@link OfflinePlayer}.
     * <p>
     * This method converts the 8x8 grid of {@link BaseComponent} objects into a legacy text format
     * using {@link TextComponent#toLegacyText(BaseComponent[])}.
     * </p>
     *
     * @param player     the {@link OfflinePlayer} whose head is to be retrieved.
     * @param overlay    {@code true} to apply the skin overlay; {@code false} otherwise.
     * @param skinSource the {@link SkinSource} to use for retrieving the player's skin.
     * @return a legacy-formatted string representing the player's head.
     */
    public String getHeadAsString(OfflinePlayer player, boolean overlay, SkinSource skinSource) {
        return TextComponent.toLegacyText(
                this.getHead(player, overlay, skinSource)
        );
    }

    /**
     * Retrieves the player's head as a legacy-formatted string using the specified {@link OfflinePlayer}.
     * <p>
     * This method applies the skin overlay and uses the default skin source before converting
     * the 8x8 grid of {@link BaseComponent} objects into a legacy text format via
     * {@link TextComponent#toLegacyText(BaseComponent[])}.
     * </p>
     *
     * @param player the {@link OfflinePlayer} whose head is to be retrieved.
     * @return a legacy-formatted string representing the player's head.
     */
    public String getHeadAsString(OfflinePlayer player) {
        return getHeadAsString(player, true, defaultSource);
    }
}
