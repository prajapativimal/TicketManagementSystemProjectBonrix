package com.example.demo.config;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .cors().and() // <--- STEP 1: ENABLE CORS
        .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
            .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**"
                ).permitAll() // allow swagger
            // Admin register/login
            .requestMatchers("/api/admin/register", "/api/admin/login").permitAll()
            // Merchant register/login/OTP
            .requestMatchers(
                "/api/merchant/register",
                "/api/merchant/login",
                "/api/merchant/request-otp"
            ).permitAll()
            
            .requestMatchers(
            	    "/api/support-agent/register",
            	    "/api/support-agent/login"
            	).permitAll()
            .requestMatchers("/messages/**").permitAll() 

            // Admin endpoints require ADMIN role
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            
            // ✅ ADD THIS RULE to make the brands endpoint public
            .requestMatchers("/api/merchant/brands").permitAll()
            
            .requestMatchers("/images/**").permitAll() 
            
            // ✅ ADD THIS NEW LINE to allow access to message attachments
        //subadmin 
            // ✅ ADD THIS LINE TO MAKE YOUR NEW LOGIN API PUBLIC
            .requestMatchers("/api/SubAdminbrand/**").permitAll() 
            

            
            
         // In SecurityConfig.java
            .requestMatchers("/api/support-agent/**").hasAuthority("SUPPORT_AGENT")
            // Merchant endpoints require MERCHANT role
            // .requestMatchers("/api/merchant/**").hasRole("MERCHANT") // You can uncomment this later
            // Any other request needs authentication
            .anyRequest().authenticated();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    // vvvvvv STEP 2: ADD THIS BEAN vvvvvv
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from your frontend origin
        configuration.setAllowedOrigins(Arrays.asList("*"));
        // Allow all standard methods (GET, POST, PUT, DELETE, etc.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow specific headers, including Authorization for your JWT
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all paths in your application
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    // ^^^^^^ STEP 2: ADD THIS BEAN ^^^^^^


    // Password encoder for hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication manager for login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}