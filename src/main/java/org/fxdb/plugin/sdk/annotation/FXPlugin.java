package org.fxdb.plugin.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Each class marked with this annotation will be identified as a plugin
 * and managed by the FXDB plugin framework.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXPlugin {
    String id();
}
