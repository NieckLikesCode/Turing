package de.niecklikescode.turing.api.modules;

import org.lwjgl.input.Keyboard;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Info {

    String name() default "";
    String displayName() default "";
    String description();
    int keyBind() default Keyboard.KEY_NONE;
    Module.Category category();
    int color() default -1;

    boolean toggleable() default true;

}
