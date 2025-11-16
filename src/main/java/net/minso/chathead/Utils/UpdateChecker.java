package net.minso.chathead.Utils;

import net.minso.chathead.Main;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final Main plugin;
    private final String API_URL = "https://api.github.com/repos/OGminso/ChatHeadFont/releases/latest";

    public UpdateChecker(Main plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        if (!plugin.getPluginConfig().getCheckForUpdates()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", plugin.getName() + "-Update-Checker");

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    plugin.getLogger().warning("Update check failed: HTTP " + responseCode);
                    return;
                }

                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                JSONObject json = new JSONObject(response.toString());

                String latestTag = json.optString("tag_name", "").trim();
                if (latestTag.isEmpty()) {
                    plugin.getLogger().warning("Update check failed: No tag found");
                    return;
                }

                String latestVersion = stripVersion(latestTag);
                String currentVersion = stripVersion(plugin.getDescription().getVersion());

                if (isVersionNewer(latestVersion, currentVersion)) {
                    plugin.getLogger().info("A new version of " + plugin.getName() + " is available: "
                            + latestVersion + " (current: " + currentVersion + ")");
                    plugin.getLogger().info("Download: https://github.com/OGminso/ChatHeadFont/releases/latest");
                } else {
                    plugin.getLogger().info("You are running the latest version of "
                            + plugin.getName() + " (" + currentVersion + ").");
                }

            } catch (Exception ex) {
                plugin.getLogger().warning("Could not check for updates: " + ex.getMessage());
            }
        });
    }

    private String stripVersion(String tag) {
        if (tag == null) return "";

        int vIndex = tag.lastIndexOf("v");

        if (vIndex != -1) {
            tag = tag.substring(vIndex);
        }

        if (tag.startsWith("v") || tag.startsWith("V")) {
            tag = tag.substring(1);
        }

        return tag.trim();
    }

    private boolean isVersionNewer(String latest, String current) {
        if (latest == null || current == null) {
            return false;
        }

        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");

        int max = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < max; i++) {
            int latestNum = i < latestParts.length ? parseIntSafe(latestParts[i]) : 0;
            int currentNum = i < currentParts.length ? parseIntSafe(currentParts[i]) : 0;

            if (latestNum > currentNum) return true;
            if (latestNum < currentNum) return false;
        }
        return false;
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }


}
