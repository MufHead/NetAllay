package org.allaymc.netallay.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration for NetAllay plugin.
 *
 * @author YiRanKuma
 */
@Getter
@Setter
public class NetAllayConfig {

    private static final Logger log = LoggerFactory.getLogger(NetAllayConfig.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Shop configuration.
     */
    private ShopConfig shop = new ShopConfig();

    /**
     * Shop-related configuration options.
     */
    @Getter
    @Setter
    public static class ShopConfig {
        /**
         * The game ID from NetEase developer platform (资源数字ID).
         * This is required for shop functionality.
         */
        private String gameId = "";

        /**
         * The game key for production server (正式服务器签名密钥).
         * Used for signing API requests to NetEase order server.
         */
        private String gameKey = "";

        /**
         * The game key for test server (测试服务器签名密钥).
         * Used when testServer is true.
         */
        private String testGameKey = "";

        /**
         * Whether this is a test server.
         * When true, uses test API endpoints and testGameKey.
         */
        private boolean testServer = false;

        /**
         * Whether to use custom shop mode.
         * <p>
         * When true, the default shop button is hidden.
         * When false, the official NetEase shop UI is shown.
         */
        private boolean useCustomShop = false;

        /**
         * Cache time for shop data (in seconds).
         */
        private int cacheTime = 1;

        /**
         * Custom order server URL (optional).
         * Leave empty to use default: gasproxy.mc.netease.com:60002
         */
        private String shopServerUrl = "";

        /**
         * Custom web server URL (optional).
         * Leave empty to use default: g79mclobt.nie.netease.com
         */
        private String webServerUrl = "";
    }

    /**
     * Loads the configuration from a file, or creates a default one if it doesn't exist.
     *
     * @param dataFolder the plugin's data folder
     * @return the loaded configuration
     */
    public static NetAllayConfig load(Path dataFolder) {
        Path configFile = dataFolder.resolve("config.json");

        // Create data folder if it doesn't exist
        if (!Files.exists(dataFolder)) {
            try {
                Files.createDirectories(dataFolder);
            } catch (IOException e) {
                log.error("Failed to create data folder", e);
                return new NetAllayConfig();
            }
        }

        // If config doesn't exist, create default
        if (!Files.exists(configFile)) {
            NetAllayConfig defaultConfig = new NetAllayConfig();
            defaultConfig.save(dataFolder);
            return defaultConfig;
        }

        // Load existing config
        try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
            NetAllayConfig config = GSON.fromJson(reader, NetAllayConfig.class);
            if (config == null) {
                config = new NetAllayConfig();
            }
            return config;
        } catch (IOException e) {
            log.error("Failed to load config.json", e);
            return new NetAllayConfig();
        }
    }

    /**
     * Saves the configuration to a file.
     *
     * @param dataFolder the plugin's data folder
     */
    public void save(Path dataFolder) {
        Path configFile = dataFolder.resolve("config.json");

        try {
            // Create data folder if it doesn't exist
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }

            try (Writer writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            log.error("Failed to save config.json", e);
        }
    }
}
