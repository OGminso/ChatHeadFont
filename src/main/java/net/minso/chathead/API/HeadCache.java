package net.minso.chathead.API;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code HeadCache} class is responsible for caching Minecraft player head representations
 * as arrays of {@link BaseComponent}. It reduces the frequency of asynchronous requests to fetch
 * skin data by caching previously fetched heads. Each cached head entry expires after a set duration.
 * <p>
 * The cache supports both synchronous retrieval of cached data and asynchronous refreshing of
 * expired or missing entries. A scheduled task is used to periodically remove expired cache entries.
 * </p>
 */
public class HeadCache {

    /**
     * The {@link JavaPlugin} instance associated with this cache.
     */
    private final JavaPlugin plugin;

    /**
     * The expiration time for cache entries in milliseconds (5 minutes).
     */
    private static final long CACHE_EXPIRATION = 5 * 60 * 1000; // 5 minutes

    /**
     * A map storing cached head representations, keyed by a unique combination of the player's UUID and overlay flag.
     */
    private final Map<String, CachedHead> cache = new ConcurrentHashMap<>();

    /**
     * A map used to track pending asynchronous head requests to avoid duplicate fetches.
     */
    private final Map<String, Boolean> pendingRequests = new ConcurrentHashMap<>();

    /**
     * The scheduled task responsible for cleaning up expired cache entries.
     */
    private BukkitTask cacheCleanupTask;

    /**
     * Constructs a new {@code HeadCache} instance using the specified {@link JavaPlugin}.
     * <p>
     * Upon creation, this instance immediately starts an asynchronous cleanup task to remove
     * expired cache entries.
     * </p>
     *
     * @param plugin the {@link JavaPlugin} instance associated with this cache.
     */
    public HeadCache(JavaPlugin plugin) {
        this.plugin = plugin;
        startCacheCleanupTask();
    }

    /**
     * Retrieves the cached head representation for the player identified by the specified UUID.
     * <p>
     * This method delegates to {@link #getCachedHead(OfflinePlayer, boolean, SkinSource)}.
     * </p>
     *
     * @param uuid       the UUID of the player.
     * @param overlay    {@code true} if the skin overlay should be applied; {@code false} otherwise.
     * @param skinSource the {@link SkinSource} to use for fetching the player's head if not cached.
     * @return an array of {@link BaseComponent} representing the player's head.
     */
    public BaseComponent[] getCachedHead(UUID uuid, boolean overlay, SkinSource skinSource) {
        return getCachedHead(Bukkit.getOfflinePlayer(uuid), overlay, skinSource);
    }

    /**
     * Retrieves the cached head representation for the specified {@link OfflinePlayer}.
     * <p>
     * If a valid (i.e., not expired) cached head is available, it is returned immediately.
     * Otherwise, an asynchronous task is scheduled to fetch a new head representation, and the
     * last cached version (if any) is returned. If no cached version exists, an empty array is returned.
     * </p>
     *
     * @param player     the {@link OfflinePlayer} whose head is to be retrieved.
     * @param overlay    {@code true} if the skin overlay should be applied; {@code false} otherwise.
     * @param skinSource the {@link SkinSource} to use for fetching the player's head.
     * @return an array of {@link BaseComponent} representing the player's head.
     */
    public BaseComponent[] getCachedHead(OfflinePlayer player, boolean overlay, SkinSource skinSource) {
        UUID uuid = player.getUniqueId();
        String cacheKey = getCacheKey(uuid, overlay);
        CachedHead cachedHead = cache.get(cacheKey);
        if (cachedHead != null && !isExpired(cachedHead)) {
            return cachedHead.getHead();
        }

        // Use the last cached version (even if expired) if available.
        BaseComponent[] lastHead = cachedHead != null ? cachedHead.getHead() : new BaseComponent[]{};

        // Only schedule a new asynchronous fetch if one isn't already pending.
        if (pendingRequests.putIfAbsent(cacheKey, true) == null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                BaseComponent[] head = skinSource.getHead(player, overlay);
                if (head != null && head.length > 0 && plugin.isEnabled()) {
                    cache.put(cacheKey, new CachedHead(head, overlay, System.currentTimeMillis()));
                }
                pendingRequests.remove(cacheKey);
            });
        }

        return lastHead;
    }

    /**
     * Determines whether the specified {@link CachedHead} entry has expired.
     *
     * @param cachedHead the cached head entry to check.
     * @return {@code true} if the cached head has expired; {@code false} otherwise.
     */
    private boolean isExpired(CachedHead cachedHead) {
        return System.currentTimeMillis() - cachedHead.getTimestamp() > CACHE_EXPIRATION;
    }

    /**
     * Starts an asynchronous task that periodically cleans up expired cache entries.
     * <p>
     * If a cleanup task is already running, it is cancelled before starting a new one.
     * </p>
     */
    private void startCacheCleanupTask() {
        if (cacheCleanupTask != null) {
            cacheCleanupTask.cancel();
        }

        cacheCleanupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            cache.entrySet().removeIf(entry -> isExpired(entry.getValue()));
        }, CACHE_EXPIRATION / 20, CACHE_EXPIRATION / 20);
    }

    /**
     * Generates a unique cache key based on the player's UUID and the overlay flag.
     *
     * @param uuid    the UUID of the player.
     * @param overlay {@code true} if the skin overlay is applied; {@code false} otherwise.
     * @return a unique string key used for caching purposes.
     */
    private String getCacheKey(UUID uuid, boolean overlay) {
        return uuid.toString() + ":" + overlay;
    }

    /**
     * A helper class representing a cached head entry.
     * <p>
     * Each {@code CachedHead} instance stores the head representation as an array of {@link BaseComponent},
     * whether the head was generated with a skin overlay, and the timestamp when the head was cached.
     * </p>
     */
    private static class CachedHead {
        /**
         * The head representation as an array of {@link BaseComponent}.
         */
        private final BaseComponent[] head;

        /**
         * Indicates whether the head representation was generated with a skin overlay.
         */
        private final boolean overlay;

        /**
         * The timestamp (in milliseconds) when this head was cached.
         */
        private final long timestamp;

        /**
         * Constructs a new {@code CachedHead} instance.
         *
         * @param head      the head representation as an array of {@link BaseComponent}.
         * @param overlay   {@code true} if the head was generated with an overlay; {@code false} otherwise.
         * @param timestamp the time at which the head was cached (in milliseconds).
         */
        CachedHead(BaseComponent[] head, boolean overlay, long timestamp) {
            this.head = head;
            this.overlay = overlay;
            this.timestamp = timestamp;
        }

        /**
         * Retrieves the cached head representation.
         *
         * @return an array of {@link BaseComponent} representing the head.
         */
        public BaseComponent[] getHead() {
            return head;
        }

        /**
         * Indicates whether the head was generated with an overlay.
         *
         * @return {@code true} if an overlay was applied; {@code false} otherwise.
         */
        public boolean hasOverlay() {
            return overlay;
        }

        /**
         * Retrieves the timestamp when the head was cached.
         *
         * @return the timestamp in milliseconds.
         */
        public long getTimestamp() {
            return timestamp;
        }
    }
}
