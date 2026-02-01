package org.allaymc.netallay.shop;

/**
 * Constants used by the NetEase shop system.
 *
 * @author YiRanKuma
 */
public final class ShopConstants {

    private ShopConstants() {
        // Utility class
    }

    // ==================== Namespaces and Systems ====================

    /**
     * The namespace for shop-related events.
     */
    public static final String SHOP_NAMESPACE = "neteaseShop";

    /**
     * The server-side system name for shop events (used when sending to client).
     */
    public static final String SHOP_SERVER_SYSTEM = "neteaseShopDev";

    /**
     * The client-side system name for shop events (used when receiving from client).
     */
    public static final String SHOP_CLIENT_SYSTEM = "neteaseShopBeh";

    // ==================== Client to Server Events ====================

    /**
     * Event sent by client when entering the shop.
     * Server should respond with {@link #EVENT_SERVER_READY}.
     */
    public static final String EVENT_CLIENT_ENTER = "clientEnterEvent";

    /**
     * Event sent by client when player requests to ship items (urge shipping).
     */
    public static final String EVENT_CLIENT_FORCE_SHIP = "clientForceShipEvent";

    /**
     * Event sent by client when player successfully purchases an item.
     */
    public static final String EVENT_CLIENT_BUY_SUCCESS = "clientBuyItemPaySuccessEvent";

    // ==================== Server to Client Events ====================

    /**
     * Event sent by server to indicate shop is ready.
     * Contains shop configuration data.
     */
    public static final String EVENT_SERVER_READY = "serverReadyEvent";

    /**
     * Event sent by server to open the shop UI.
     */
    public static final String EVENT_OPEN_SHOP = "OpenShopEvent";

    /**
     * Event sent by server to close the shop UI.
     */
    public static final String EVENT_CLOSE_SHOP = "CloseShopEvent";

    /**
     * Event sent by server to show a hint message.
     */
    public static final String EVENT_SHOW_HINT = "ShowHintEvent";

    // ==================== Engine Namespace ====================

    /**
     * The Minecraft engine namespace.
     */
    public static final String ENGINE_NAMESPACE = "Minecraft";

    /**
     * The engine system name.
     */
    public static final String ENGINE_SYSTEM = "Engine";

    /**
     * The chat extension system name.
     */
    public static final String CHAT_EXTENSION_SYSTEM = "chatExtension";

    // ==================== Engine Events ====================

    /**
     * Engine event: Client finished loading addons.
     */
    public static final String ENGINE_EVENT_ADDONS_LOADED = "ClientLoadAddonsFinishedFromGac";

    /**
     * Engine event: Player requests to ship items.
     */
    public static final String ENGINE_EVENT_URGE_SHIP = "UrgeShipEvent";

    /**
     * Engine event: Store purchase succeeded.
     */
    public static final String ENGINE_EVENT_BUY_SUCCESS = "StoreBuySuccServerEvent";
}
