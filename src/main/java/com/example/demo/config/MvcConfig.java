// Create a new file: config/MvcConfig.java
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	 @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        // --- Handler for Complaint attachments in /uploads/images/ ---
	        registry.addResourceHandler("/images/**")
	                .addResourceLocations("file:" + Paths.get("uploads/images").toAbsolutePath() + "/");

	        // ✅ ADD THIS NEW HANDLER for Message attachments in /uploads/messages/
	        registry.addResourceHandler("/messages/**")
	                .addResourceLocations("file:" + Paths.get("uploads/messages").toAbsolutePath() + "/");
	    }
}