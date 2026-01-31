package org.allaymc.netallay.handler;

import org.allaymc.netallay.PyRpcHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing PyRpc event listeners.
 * <p>
 * This class is thread-safe and supports registering multiple handlers for the same event.
 *
 * @author YiRanKuma
 */
public final class EventListenerRegistry {

    private final Map<String, List<PyRpcHandler>> handlers = new ConcurrentHashMap<>();

    /**
     * Generates a unique key for an event.
     *
     * @param namespace  the event namespace
     * @param systemName the system name
     * @param eventName  the event name
     * @return a unique key string
     */
    public static String createEventKey(String namespace, String systemName, String eventName) {
        return namespace + ":" + systemName + ":" + eventName;
    }

    /**
     * Registers a handler for an event.
     *
     * @param namespace  the event namespace
     * @param systemName the system name
     * @param eventName  the event name
     * @param handler    the handler to register
     */
    public void register(String namespace, String systemName, String eventName, PyRpcHandler handler) {
        String key = createEventKey(namespace, systemName, eventName);
        handlers.computeIfAbsent(key, k -> new ArrayList<>()).add(handler);
    }

    /**
     * Unregisters a handler for an event.
     *
     * @param namespace  the event namespace
     * @param systemName the system name
     * @param eventName  the event name
     * @param handler    the handler to unregister
     * @return true if the handler was found and removed
     */
    public boolean unregister(String namespace, String systemName, String eventName, PyRpcHandler handler) {
        String key = createEventKey(namespace, systemName, eventName);
        List<PyRpcHandler> list = handlers.get(key);
        if (list != null) {
            return list.remove(handler);
        }
        return false;
    }

    /**
     * Unregisters all handlers for an event.
     *
     * @param namespace  the event namespace
     * @param systemName the system name
     * @param eventName  the event name
     */
    public void unregisterAll(String namespace, String systemName, String eventName) {
        String key = createEventKey(namespace, systemName, eventName);
        handlers.remove(key);
    }

    /**
     * Gets all handlers for an event.
     *
     * @param namespace  the event namespace
     * @param systemName the system name
     * @param eventName  the event name
     * @return a list of handlers (may be empty, never null)
     */
    public List<PyRpcHandler> getHandlers(String namespace, String systemName, String eventName) {
        String key = createEventKey(namespace, systemName, eventName);
        List<PyRpcHandler> list = handlers.get(key);
        return list != null ? new ArrayList<>(list) : List.of();
    }

    /**
     * Checks if any handlers are registered for an event.
     *
     * @param namespace  the event namespace
     * @param systemName the system name
     * @param eventName  the event name
     * @return true if at least one handler is registered
     */
    public boolean hasHandlers(String namespace, String systemName, String eventName) {
        String key = createEventKey(namespace, systemName, eventName);
        List<PyRpcHandler> list = handlers.get(key);
        return list != null && !list.isEmpty();
    }

    /**
     * Clears all registered handlers.
     */
    public void clear() {
        handlers.clear();
    }

    /**
     * Gets the total number of registered event types.
     *
     * @return the number of unique event types with handlers
     */
    public int getRegisteredEventCount() {
        return handlers.size();
    }
}
