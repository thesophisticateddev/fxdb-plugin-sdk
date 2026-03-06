package org.fxdb.plugin.sdk.event;

import javafx.event.Event;

/**
 * Abstraction for the event bus. The host application provides
 * the implementation; plugins fire events through this interface.
 *
 * <p>The host app must call {@link #setInstance(PluginEventBus)} at startup
 * before any plugins are loaded.</p>
 */
public interface PluginEventBus {

    /**
     * Fires an event to all registered handlers.
     */
    void fireEvent(Event event);

    // --- Static singleton holder ---

    class Holder {
        static volatile PluginEventBus instance;
    }

    /**
     * Sets the global PluginEventBus instance.
     * Called by the host application at startup.
     */
    static void setInstance(PluginEventBus bus) {
        Holder.instance = bus;
    }

    /**
     * Returns the global PluginEventBus instance, or null if not yet configured.
     */
    static PluginEventBus getInstance() {
        return Holder.instance;
    }
}
