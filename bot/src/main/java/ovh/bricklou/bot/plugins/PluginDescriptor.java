package ovh.bricklou.bot.plugins;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginDescriptor {
    String name();

    String version();

    String author();

    String dependencies() default "";
}
