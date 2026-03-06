# FXDB Plugin SDK

The official SDK for building plugins for [FXDB](https://github.com/user/fxdb), a JavaFX database client. This SDK provides the interfaces, annotations, and utilities needed to develop, package, and distribute FXDB plugins independently from the main application.

## Requirements

- Java 17+
- Maven 3.8+
- JavaFX 17 (provided by the host application at runtime)

## Installation

Add the SDK as a **provided** dependency in your plugin's `pom.xml`:

```xml
<dependency>
    <groupId>org.fxdb</groupId>
    <artifactId>fxdb-plugin-sdk</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

> **Why `provided`?** The host application already includes the SDK on its classpath. Your plugin JAR should not bundle it — only compile against it.

## Quick Start

### 1. Create a Maven Project

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>fxdb-plugin-myplugin</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <javafx.version>17.0.6</javafx.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.fxdb</groupId>
            <artifactId>fxdb-plugin-sdk</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

### 2. Write Your Plugin Class

Every plugin must:

1. Extend `AbstractPlugin`
2. Be annotated with `@FXPlugin(id = "...")`
3. Implement `getId()`, `getName()`, `getVersion()`
4. Implement `onInitialize()`, `onStart()`, `onStop()`

```java
package com.example;

import javafx.application.Platform;
import org.fxdb.plugin.sdk.AbstractPlugin;
import org.fxdb.plugin.sdk.annotation.FXPlugin;

@FXPlugin(id = "my-plugin")
public class MyPlugin extends AbstractPlugin {

    @Override
    public String getId() { return "my-plugin"; }

    @Override
    public String getName() { return "My Plugin"; }

    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    protected void onInitialize() {
        // Called once when the plugin is loaded. No UI access here.
        logger.info("MyPlugin initialized");
    }

    @Override
    protected void onStart() {
        // Called in a separate daemon thread.
        // Use Platform.runLater() for any UI work.
        Platform.runLater(() -> {
            logger.info("MyPlugin started");
        });
    }

    @Override
    protected void onStop() {
        // Clean up resources, close connections, remove UI elements.
        logger.info("MyPlugin stopped");
    }
}
```

### 3. Build and Install

```bash
mvn clean package
```

Copy the resulting JAR into the FXDB plugins directory:

```bash
cp target/fxdb-plugin-myplugin-1.0.0.jar ~/.fxdb/plugins/
```

The plugin will be discovered and loaded on the next FXDB startup.

## Plugin Lifecycle

Plugins go through the following states:

```
AVAILABLE ─── installPlugin() ───> INSTALLED
                                       │
                                  loadPlugin()
                                       │
                                       v
                                   LOADING
                                       │
                                   onStart()
                                       │
                                       v
                                    RUNNING
                                       │
                                   onStop()
                                       │
                                       v
                                   INSTALLED ──── uninstall ───> AVAILABLE
```

| Callback | Thread | Purpose |
|---|---|---|
| `onInitialize()` | Loader thread | One-time setup, no UI |
| `onStart()` | Daemon thread | Start operations, register UI (via `Platform.runLater`) |
| `onStop()` | Daemon thread | Cleanup resources, remove UI elements |

## API Reference

### Core Classes

| Class | Package | Description |
|---|---|---|
| `IPlugin` | `org.fxdb.plugin.sdk` | Interface all plugins implement |
| `AbstractPlugin` | `org.fxdb.plugin.sdk` | Base class with lifecycle, threading, and logging |
| `PluginApiVersion` | `org.fxdb.plugin.sdk` | SDK version constants for compatibility checks |

### Annotations

| Annotation | Target | Description |
|---|---|---|
| `@FXPlugin(id)` | Class | Marks a class as a plugin. The `id` must be unique. |
| `@FXPluginStart` | Method | Marks a method as a startup hook (runs after constructor + DI) |
| `@FXPluginDependency(getName)` | Constructor param | Injects a dependency by name |
| `@FXPluginDependencyFactory` | Class | Marks a class as a dependency provider |
| `@FXDependencyInstance` | Method | Marks a method inside a factory that returns a dependency instance |
| `@FXPluginHandleEvent` | Method | Marks a method as an event handler |

### Models

| Class | Package | Description |
|---|---|---|
| `PluginInfo` | `org.fxdb.plugin.sdk.model` | Plugin metadata (id, name, version, status, etc.) |
| `PluginManifest` | `org.fxdb.plugin.sdk.model` | Manifest containing a list of `PluginInfo` entries |

### Events

| Class | Package | Description |
|---|---|---|
| `PluginEvent` | `org.fxdb.plugin.sdk.event` | JavaFX `Event` subclass fired on lifecycle changes |
| `PluginEventBus` | `org.fxdb.plugin.sdk.event` | Static event bus abstraction (host app provides the implementation) |
| `IEvent` | `org.fxdb.plugin.sdk.event` | Simple interface with `getMessage()` |

**PluginEvent types:** `PLUGIN_INSTALLED`, `PLUGIN_UNINSTALLED`, `PLUGIN_STARTED`, `PLUGIN_STOPPED`, `PLUGIN_ERROR`, `PLUGIN_LOADED`

### Runtime

| Class | Package | Description |
|---|---|---|
| `FXPluginRegistry` | `org.fxdb.plugin.sdk.runtime` | Singleton registry for shared instances between host and plugins |

### UI

| Class | Package | Description |
|---|---|---|
| `PluginUIContext` | `org.fxdb.plugin.sdk.ui` | Thread-safe access to the host app's TabPane and TreeView |

## Working with the UI

Plugins interact with the FXDB interface through `PluginUIContext`, retrieved from the registry during startup:

```java
@Override
protected void onStart() {
    Platform.runLater(() -> {
        PluginUIContext ctx = FXPluginRegistry.INSTANCE.get("ui.context", PluginUIContext.class);

        // Add a node to the sidebar tree
        TreeItem<String> root = new TreeItem<>("My Data Source");
        ctx.addBrowserNode(root);

        // Add a tab to the main tab pane
        Tab tab = new Tab("My Tab", new Label("Hello from plugin!"));
        ctx.addTab(tab);
    });
}

@Override
protected void onStop() {
    Platform.runLater(() -> {
        PluginUIContext ctx = FXPluginRegistry.INSTANCE.get("ui.context", PluginUIContext.class);
        ctx.removeBrowserNode(root);
        ctx.removeTab(tab);
    });
}
```

**Key rules:**
- Always retrieve `PluginUIContext` inside `onStart()`, not `onInitialize()` — the UI may not be ready yet.
- Wrap all UI operations in `Platform.runLater()` since `onStart()` runs on a daemon thread.
- Clean up everything in `onStop()` — remove tabs, tree nodes, and event handlers.

## Dependency Injection

The SDK supports constructor-based dependency injection for plugins that depend on shared services or other plugins.

### Providing Dependencies

```java
@FXPluginDependencyFactory
public class MyDependencies {

    @FXDependencyInstance
    public DatabaseService databaseService() {
        return new DatabaseService("localhost", 5432);
    }
}
```

### Consuming Dependencies

```java
@FXPlugin(id = "data-export")
public class DataExportPlugin extends AbstractPlugin {

    private final DatabaseService db;

    public DataExportPlugin(
            @FXPluginDependency(getName = "databaseService") DatabaseService db) {
        this.db = db;
    }

    // ... lifecycle methods
}
```

The microkernel resolves dependencies via topological sort — circular dependencies are detected and logged.

## Using the Plugin Registry

`FXPluginRegistry.INSTANCE` is a thread-safe singleton shared between the host and all plugins. The host pre-registers useful instances:

```java
// Read a shared instance
DatabaseManager dbManager = FXPluginRegistry.INSTANCE.get("databaseManager", DatabaseManager.class);

// Register your own instance for other plugins to use
FXPluginRegistry.INSTANCE.addInstance("myService", myServiceInstance);
```

**Pre-registered keys by the host app:**

| Key | Type | Description |
|---|---|---|
| `"ui.context"` | `PluginUIContext` | Access to the main UI components |
| `"databaseManager"` | `DatabaseManager` | Access to the active database connections |

## Packaging Plugins with External Libraries

If your plugin uses third-party libraries (e.g., a JDBC driver or a NoSQL client), use the `maven-shade-plugin` to create a fat JAR that includes those dependencies. Relocate packages to avoid classpath conflicts:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals><goal>shade</goal></goals>
                    <configuration>
                        <filters>
                            <filter>
                                <artifact>*:*</artifact>
                                <excludes>
                                    <exclude>META-INF/*.SF</exclude>
                                    <exclude>META-INF/*.DSA</exclude>
                                    <exclude>META-INF/*.RSA</exclude>
                                </excludes>
                            </filter>
                        </filters>
                        <relocations>
                            <relocation>
                                <pattern>com.mongodb</pattern>
                                <shadedPattern>com.example.shaded.com.mongodb</shadedPattern>
                            </relocation>
                        </relocations>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**Do not shade** the SDK or JavaFX — those are provided by the host.

## Plugin Manifest

Each plugin is described in a `plugin-manifest.json` file that the host application reads:

```json
{
  "manifestVersion": "1.0.0",
  "lastUpdated": "2026-03-06T00:00:00",
  "plugins": [
    {
      "id": "my-plugin",
      "name": "My Plugin",
      "version": "1.0.0",
      "description": "A short description of what this plugin does.",
      "author": "Your Name",
      "category": "Tools",
      "mainClass": "com.example.MyPlugin",
      "jarFile": "fxdb-plugin-myplugin-1.0.0.jar",
      "enabled": true,
      "installed": false,
      "dependencies": [],
      "status": "AVAILABLE"
    }
  ]
}
```

## Project Structure

```
fxdb-plugin-sdk/
  src/main/java/org/fxdb/plugin/sdk/
    IPlugin.java                          # Core plugin interface
    AbstractPlugin.java                   # Base class with lifecycle management
    PluginApiVersion.java                 # SDK version constants
    annotation/
      FXPlugin.java                       # @FXPlugin(id = "...")
      FXPluginStart.java                  # @FXPluginStart
      FXPluginDependency.java             # @FXPluginDependency(getName = "...")
      FXPluginDependencyFactory.java      # @FXPluginDependencyFactory
      FXDependencyInstance.java           # @FXDependencyInstance
      FXPluginHandleEvent.java            # @FXPluginHandleEvent
    model/
      PluginInfo.java                     # Plugin metadata model
      PluginManifest.java                 # Manifest model
    event/
      IEvent.java                         # Event interface
      PluginEvent.java                    # Lifecycle event (extends JavaFX Event)
      PluginEventBus.java                 # Event bus abstraction
    runtime/
      FXPluginRegistry.java              # Shared instance registry
    ui/
      PluginUIContext.java               # Thread-safe UI access
```

## Versioning

The SDK follows [Semantic Versioning](https://semver.org/):

- **MAJOR** — Breaking API changes. Plugins must be recompiled.
- **MINOR** — New features, backward compatible. Existing plugins continue to work.
- **PATCH** — Bug fixes only.

Check compatibility at runtime:

```java
if (PluginApiVersion.MAJOR != expectedMajor) {
    logger.warning("SDK version mismatch — plugin may not work correctly");
}
```

## License

See [LICENSE](LICENSE) for details.
