package pro.xite.dev.vkgram.util.statekeeper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME) @Target(FIELD)
public @interface KeepState {
    String DUMMY_KEY = "DUMMY_KEY";
    String value() default DUMMY_KEY;
}