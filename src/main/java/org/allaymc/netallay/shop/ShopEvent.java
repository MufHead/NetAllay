package org.allaymc.netallay.shop;

/**
 * Enumeration of shop-related events that plugins can listen to.
 * <p>
 * Use {@link ShopManager#listenForShopEvent(ShopEvent, ShopEventHandler)} to register handlers.
 *
 * @author YiRanKuma
 */
public enum ShopEvent {

    /**
     * Fired when a player successfully purchases an item from the shop.
     * <p>
     * This event is triggered by the client after a successful payment.
     * The server should then query the order list and deliver the items.
     */
    PLAYER_BUY_ITEM_SUCCESS("player_buy_item_success"),

    /**
     * Fired when a player requests to expedite item delivery (urge shipping).
     * <p>
     * This typically means the player clicked a "ship now" or similar button.
     * The server should check for pending orders and attempt delivery.
     */
    PLAYER_URGE_SHIP("player_urge_ship"),

    /**
     * Fired when the client has finished loading all addons.
     * <p>
     * This is a good time to send initial configuration or sync data to the client.
     */
    CLIENT_LOAD_ADDON_FINISH("client_load_addon_finish");

    private final String eventName;

    ShopEvent(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets the internal event name used for registration.
     *
     * @return the event name string
     */
    public String getEventName() {
        return eventName;
    }
}
