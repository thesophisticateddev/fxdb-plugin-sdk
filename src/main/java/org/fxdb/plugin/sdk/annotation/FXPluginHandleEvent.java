package org.fxdb.plugin.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is a handler for events.
 * Event handler methods are used to respond to events triggered within the plugin framework.
 * These methods are automatically invoked when an event they are registered for occurs.
 *
 * <p> Constraints </p>
 * <ul>
 *  <li> Class has to be annotated with {@link FXPlugin} </li>
 *  <li> Method must have one parameter of type Object</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXPluginHandleEvent {
}
