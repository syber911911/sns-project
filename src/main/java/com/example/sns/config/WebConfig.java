package com.example.sns.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile/image/**")
                .addResourceLocations("file:images/profiles/")
                .setCachePeriod(20);
        registry.addResourceHandler("/article/image/**")
                .addResourceLocations("file:images/article/")
                .setCachePeriod(20);
    }
}
