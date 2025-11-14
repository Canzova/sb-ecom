package com.ecommerce.security;

import com.ecommerce.security.jwt.AuthEntryPointJwt;
import com.ecommerce.security.jwt.AuthTokenFilter;
import com.ecommerce.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // A way to get user
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    // Used When any unauthorized user tires to access any resource
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // This is a custom filter which we have created, it will check if the token is valid or not
    @Bean
    public AuthTokenFilter authenticationJwtToken(){
        return new AuthTokenFilter();
    }

//    @Autowired
//    public AuthTokenFilter authenticationJwtToken;

    /*
    2️ DaoAuthenticationProvider does the following:

    🟢 Takes the username & password sent by the login request
    🟢 Calls your UserDetailsServiceImpl.loadUserByUsername(...)
    🟢 Compares the password using the password encoder (BCrypt)
    🟢 If correct → user is authenticated
    🟢 YES — it uses your custom UserDetailsServiceImpl to authenticate users.
     */

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider =  new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Spring security wants to have password encrypted and this is an encryption algo
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
                .exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());

        // Spring Security has many filters, and we have created one our own custom filter which assigns JWTs to users and
        // register them in spring security context with their details like, name, roles, session timeout etc

        // Use this filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtToken(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ALl these urls will bypass the spring security completely
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web -> web.ignoring().requestMatchers(
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"
        ));
    }
}
