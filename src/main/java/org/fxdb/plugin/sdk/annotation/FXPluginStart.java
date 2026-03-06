package org.fxdb.plugin.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates a method as a plugin start hook.
 * Plugin start hooks are methods that will be automatically triggered when a plugin
 * starts its lifecycle.
 *
 * <p> Order of execution in plugin class </p>
 * <ul>
 *  <li> initialize dependencies </li>
 *  <li> run constructor </li>
 *  <li> initialize extension points</li>
 *  <li> run @FXPluginStart method</li>
 * </ul>
 *
 * Class has to be declared as plugin for this annotation to work.
 * If a class has multiple methods marked with this annotation, they will be
 * executed in the order they are declared.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXPluginStart {
}
