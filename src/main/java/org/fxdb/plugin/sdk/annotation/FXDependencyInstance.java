package org.fxdb.plugin.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a method as a factory for a dependency.
 * The method annotated with this annotation returns an instance of the specified dependency.
 *
 * <p> Constraints </p>
 * <ul>
 *  <li> Class has to have {@link FXPluginDependencyFactory} annotation for this annotation to work. </li>
 *  <li> Method name is used as the identifier for the dependency. </li>
 *  <li> Method must have no parameters.</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXDependencyInstance {
}
