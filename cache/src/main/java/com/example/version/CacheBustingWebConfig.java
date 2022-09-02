package com.example.version;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheBustingWebConfig implements WebMvcConfigurer {

    public static final String PREFIX_STATIC_RESOURCES = "/resources";

    private final ResourceVersion resourceVersion;

    public CacheBustingWebConfig(final ResourceVersion resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        final CacheControl cacheControl = CacheControl
                .maxAge(Duration.ofDays(365))
                .cachePublic();

        registry.addResourceHandler(PREFIX_STATIC_RESOURCES + "/" + resourceVersion.getVersion() + "/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(cacheControl);
    }
}
