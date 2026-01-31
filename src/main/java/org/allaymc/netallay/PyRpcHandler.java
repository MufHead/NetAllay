package org.allaymc.netallay;

import org.allaymc.api.player.Player;

import java.util.Map;

/**
 * Handler interface for receiving PyRpc events from NetEase clients.
 * <p>
 * Implement this interface to handle client-to-server events.
 * <p>
 * Example usage:
 * <pre>{@code
 * NetAllay.getInstance().listenForEvent("MyMod", "MySystem", "MyEvent", (player, data) -> {
 *     // Handle the event
 *     String value = (String) data.get("someKey");
 *     // Process the data...
 * });
 * }</pre>
 *
 * @author YiRanKuma
 */
@FunctionalInterface
public interface PyRpcHandler {

    /**
     * Called when a PyRpc event is received from a client.
     *
     * @param player    the player who sent the event
     * @param eventData the event data as a Map. Keys are always strings,
     *                  values can be: Boolean, Long, Double, String, List, or nested Map
     */
    void handle(Player player, Map<String, Object> eventData);
}
