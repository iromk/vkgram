package pro.xite.dev.vkgram.di.modules;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Roman Syrchin on 7/7/18.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface VkApiVersion {
    String value() default "default";
}
