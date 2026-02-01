package org.allaymc.netallay.shop;

import org.allaymc.api.player.Player;

import java.util.Map;

/**
 * Handler interface for shop events.
 * <p>
 * Implement this interface to handle shop-related events like purchases or shipping requests.
 *
 * @author YiRanKuma
 */
@FunctionalInterface
public interface ShopEventHandler {

    /**
     * Called when a shop event occurs.
     *
     * @param player the player associated with the event
     * @param data   optional event data (may be null for some events)
     */
    void onEvent(Player player, Map<String, Object> data);
}
