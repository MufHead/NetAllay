package org.allaymc.netallay.shop;

import lombok.Getter;
import lombok.Setter;
import org.allaymc.api.player.Player;
import org.allaymc.netallay.NetAllay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager for NetEase shop functionality.
 * <p>
 * This class handles shop-related events, configurations, and provides APIs
 * for controlling the shop UI and processing orders.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * ShopManager shop = NetAllay.getInstance().getShopManager();
 *
 * // Enable custom shop (hide default shop button)
 * shop.setUseCustomShop(true);
 *
 * // Listen for purchase events
 * shop.listenForShopEvent(ShopEvent.PLAYER_BUY_ITEM_SUCCESS, (player, data) -> {
 *     // Query order list and deliver items
 * });
 *
 * // Open shop for a player
 * shop.openShop(player);
 * }</pre>
 *
 * @author YiRanKuma
 */
public class ShopManager {

    private static final Logger log = LoggerFactory.getLogger(ShopManager.class);

    private final NetAllay plugin;

    /**
     * The game ID from NetEase developer platform.
     */
    @Getter
    @Setter
    private String gameId = "";

    /**
     * The game key for production server (used for API signing).
     */
    @Getter
    @Setter
    private String gameKey = "";

    /**
     * The game key for test server.
     */
    @Getter
    @Setter
    private String testGameKey = "";

    /**
     * Whether this is a test server.
     */
    @Getter
    @Setter
    private boolean testServer = false;

    /**
     * Whether to use custom shop mode.
     * <p>
     * When true, the default shop button is hidden and you control the shop UI yourself.
     * When false, the official NetEase shop UI is used.
     */
    @Getter
    @Setter
    private boolean useCustomShop = false;

    /**
     * Cache time for shop data (in seconds).
     */
    @Getter
    @Setter
    private int cacheTime = 1;

    /**
     * Custom order server URL (optional).
     */
    @Getter
    @Setter
    private String shopServerUrl = "";

    /**
     * Custom web server URL (optional).
     */
    @Getter
    @Setter
    private String webServerUrl = "";

    /**
     * Map of shop event handlers.
     */
    private final Map<String, List<ShopEventHandler>> eventHandlers = new ConcurrentHashMap<>();

    /**
     * Per-player shop configurations (for advanced use cases).
     */
    private final Map<UUID, PlayerShopConfig> playerConfigs = new ConcurrentHashMap<>();

    public ShopManager(NetAllay plugin) {
        this.plugin = plugin;
        registerInternalListeners();
    }

    // ==================== Event Listener Registration ====================

    /**
     * Registers a handler for a shop event.
     *
     * @param event   the shop event to listen for
     * @param handler the handler to call when the event occurs
     */
    public void listenForShopEvent(ShopEvent event, ShopEventHandler handler) {
        eventHandlers.computeIfAbsent(event.getEventName(), k -> new ArrayList<>()).add(handler);
        log.debug("Registered shop event handler for: {}", event.getEventName());
    }

    /**
     * Unregisters a handler for a shop event.
     *
     * @param event   the shop event
     * @param handler the handler to remove
     * @return true if the handler was found and removed
     */
    public boolean unlistenForShopEvent(ShopEvent event, ShopEventHandler handler) {
        List<ShopEventHandler> handlers = eventHandlers.get(event.getEventName());
        if (handlers != null) {
            return handlers.remove(handler);
        }
        return false;
    }

    /**
     * Fires a shop event to all registered handlers.
     *
     * @param event  the event type
     * @param player the player associated with the event
     * @param data   optional event data
     */
    public void fireShopEvent(ShopEvent event, Player player, Map<String, Object> data) {
        List<ShopEventHandler> handlers = eventHandlers.get(event.getEventName());
        if (handlers != null) {
            for (ShopEventHandler handler : handlers) {
                try {
                    handler.onEvent(player, data);
                } catch (Exception e) {
                    log.error("Error in shop event handler for {}", event.getEventName(), e);
                }
            }
        }
    }

    // ==================== Shop Control API ====================

    /**
     * Opens the shop UI for a player.
     *
     * @param player the player
     * @return true if the packet was sent successfully
     */
    public boolean openShop(Player player) {
        return plugin.notifyToClient(
                player,
                ShopConstants.SHOP_NAMESPACE,
                ShopConstants.SHOP_SERVER_SYSTEM,
                ShopConstants.EVENT_OPEN_SHOP,
                new HashMap<>()
        );
    }

    /**
     * Closes the shop UI for a player.
     *
     * @param player the player
     * @return true if the packet was sent successfully
     */
    public boolean closeShop(Player player) {
        return plugin.notifyToClient(
                player,
                ShopConstants.SHOP_NAMESPACE,
                ShopConstants.SHOP_SERVER_SYSTEM,
                ShopConstants.EVENT_CLOSE_SHOP,
                new HashMap<>()
        );
    }

    /**
     * Shows a single-line hint message to a player.
     *
     * @param player the player
     * @param text   the hint text
     * @return true if the packet was sent successfully
     */
    public boolean showHint(Player player, String text) {
        Map<String, Object> data = new HashMap<>();
        List<String> hints = new ArrayList<>();
        hints.add(text != null ? text : "");
        data.put("hint", hints);

        return plugin.notifyToClient(
                player,
                ShopConstants.SHOP_NAMESPACE,
                ShopConstants.SHOP_SERVER_SYSTEM,
                ShopConstants.EVENT_SHOW_HINT,
                data
        );
    }

    /**
     * Shows a two-line hint message to a player.
     *
     * @param player the player
     * @param head   the first line (header)
     * @param tail   the second line (can be null)
     * @return true if the packet was sent successfully
     */
    public boolean showHint(Player player, String head, String tail) {
        Map<String, Object> data = new HashMap<>();
        List<String> hints = new ArrayList<>();
        hints.add(head != null ? head : "");
        hints.add(tail != null ? tail : "");
        data.put("hint", hints);

        return plugin.notifyToClient(
                player,
                ShopConstants.SHOP_NAMESPACE,
                ShopConstants.SHOP_SERVER_SYSTEM,
                ShopConstants.EVENT_SHOW_HINT,
                data
        );
    }

    // ==================== Per-Player Configuration ====================

    /**
     * Sets a custom shop configuration for a specific player.
     * <p>
     * Use this for advanced scenarios where different players need different shop settings.
     *
     * @param player the player
     * @param config the configuration
     */
    public void setPlayerConfig(Player player, PlayerShopConfig config) {
        playerConfigs.put(player.getControlledEntity().getUniqueId(), config);
    }

    /**
     * Gets the shop configuration for a player.
     *
     * @param player the player
     * @return the player's config, or null if using defaults
     */
    public PlayerShopConfig getPlayerConfig(Player player) {
        return playerConfigs.get(player.getControlledEntity().getUniqueId());
    }

    /**
     * Removes a player's custom configuration.
     *
     * @param player the player
     */
    public void removePlayerConfig(Player player) {
        playerConfigs.remove(player.getControlledEntity().getUniqueId());
    }

    /**
     * Clears all player configurations (typically called on plugin disable).
     */
    public void clearPlayerConfigs() {
        playerConfigs.clear();
    }

    // ==================== Internal Event Handling ====================

    /**
     * Registers internal listeners for shop-related client events.
     */
    private void registerInternalListeners() {
        // Listen for client entering shop
        plugin.listenForEvent(
                ShopConstants.SHOP_NAMESPACE,
                ShopConstants.SHOP_CLIENT_SYSTEM,
                ShopConstants.EVENT_CLIENT_ENTER,
                this::handleClientEnter
        );

        // Listen for client force ship request
        plugin.listenForEvent(
                ShopConstants.SHOP_NAMESPACE,
                ShopConstants.SHOP_CLIENT_SYSTEM,
                ShopConstants.EVENT_CLIENT_FORCE_SHIP,
                (player, data) -> fireShopEvent(ShopEvent.PLAYER_URGE_SHIP, player, data)
        );

        // Listen for client buy success
        plugin.listenForEvent(
                ShopConstants.SHOP_NAMESPACE,
                ShopConstants.SHOP_CLIENT_SYSTEM,
                ShopConstants.EVENT_CLIENT_BUY_SUCCESS,
                (player, data) -> fireShopEvent(ShopEvent.PLAYER_BUY_ITEM_SUCCESS, player, data)
        );

        log.debug("Shop internal listeners registered");
    }

    /**
     * Handles the client enter shop event.
     * Sends back the shop configuration.
     */
    private void handleClientEnter(Player player, Map<String, Object> data) {
        log.debug("Player {} entering shop", player.getOriginName());

        // Build response data
        Map<String, Object> responseData = new HashMap<>();

        // Check for per-player config first
        PlayerShopConfig playerConfig = playerConfigs.get(player.getControlledEntity().getUniqueId());

        if (playerConfig != null) {
            // Use player-specific config
            responseData.put("gameId", playerConfig.getGameId());
            responseData.put("isTestServer", playerConfig.isTestServer());
            responseData.put("useCustomShop", playerConfig.isUseCustomShop());
            responseData.put("cacheTime", playerConfig.getCacheTime());
            responseData.put("uid", playerConfig.getUid());
            responseData.put("platformUid", playerConfig.getPlatformUid());
        } else {
            // Use default config from settings
            responseData.put("gameId", gameId);
            responseData.put("isTestServer", testServer);
            responseData.put("useCustomShop", useCustomShop);
            responseData.put("cacheTime", cacheTime);
            responseData.put("uid", 0L);
            responseData.put("platformUid", "");
        }

        // Send response
        plugin.notifyToClient(
                player,
                ShopConstants.SHOP_NAMESPACE,
                ShopConstants.SHOP_SERVER_SYSTEM,
                ShopConstants.EVENT_SERVER_READY,
                responseData
        );

        log.debug("Sent shop ready response to {}: useCustomShop={}",
                player.getOriginName(), responseData.get("useCustomShop"));
    }

    /**
     * Configuration holder for per-player shop settings.
     */
    @Getter
    @Setter
    public static class PlayerShopConfig {
        private String gameId = "";
        private boolean testServer = false;
        private boolean useCustomShop = false;
        private int cacheTime = 1;
        private long uid = 0L;
        private String platformUid = "";

        public PlayerShopConfig() {
        }

        public PlayerShopConfig(String gameId, boolean useCustomShop) {
            this.gameId = gameId;
            this.useCustomShop = useCustomShop;
        }
    }
}
