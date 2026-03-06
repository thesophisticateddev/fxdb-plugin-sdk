package org.fxdb.plugin.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a class as a factory for providing dependency instances.
 * Classes marked with this annotation should define methods annotated with
 * {@link FXDependencyInstance} to provide dependency instances.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXPluginDependencyFactory {
}
