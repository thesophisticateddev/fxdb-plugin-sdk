package org.fxdb.plugin.sdk;

/**
 * Version information for the FXDB Plugin SDK.
 * Used for runtime compatibility checking between plugins and the host application.
 */
public final class PluginApiVersion {
    public static final String VERSION = "1.0.1";
    public static final int MAJOR = 1;
    public static final int MINOR = 0;
    public static final int PATCH = 1;

    private PluginApiVersion() {}
}
