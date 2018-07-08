package pro.xite.dev.vkgram.di.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Roman Syrchin on 7/8/18.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface VkApiBaseUrl {
}
