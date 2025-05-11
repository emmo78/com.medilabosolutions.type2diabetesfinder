package com.medilabosolutions.type2diabetesfinder.gatewayservice.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Class for security configuration of the application.
 * filter chain, login/logout configurations, and OAuth2 login configurations.
 *
 * @author olivier morel
 */
@Configuration
@EnableWebFluxSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .authorities("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    /**
     * Authentication success handler bean
     * Redirects to home page after successful login
     * 
     * @return ServerAuthenticationSuccessHandler
     */
    @Bean
    public ServerAuthenticationSuccessHandler authenticationSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            webFilterExchange.getExchange().getResponse()
                    .setStatusCode(HttpStatus.FOUND);
            webFilterExchange.getExchange().getResponse()
                    .getHeaders().setLocation(URI.create("/front/home"));
            log.info("User {} successfully authenticated", authentication.getName());
            return Mono.empty();
        };
    }

    /**
     * Authentication failure handler bean
     * Redirects to login page with error parameter after failed login
     * 
     * @return ServerAuthenticationFailureHandler
     */
    @Bean
    public ServerAuthenticationFailureHandler authenticationFailureHandler() {
        return (webFilterExchange, exception) -> {
            webFilterExchange.getExchange().getResponse()
                    .setStatusCode(HttpStatus.FOUND);
            webFilterExchange.getExchange().getResponse()
                    .getHeaders().setLocation(URI.create("/login?error"));
            log.warn("Authentication failed: {}", exception.getMessage());
            return Mono.empty();
        };
    }

    /**
     * Logout success handler bean
     * Redirects to login page with logout parameter after successful logout
     * 
     * @return ServerLogoutSuccessHandler
     */
    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            webFilterExchange.getExchange().getResponse()
                    .setStatusCode(HttpStatus.FOUND);
            webFilterExchange.getExchange().getResponse()
                    .getHeaders().setLocation(URI.create("/login?logout"));
            log.info("User successfully logged out");
            return Mono.empty();
        };
    }

    /**
     * Builds a SecurityWebFilterChain bean for the provided ServerHttpSecurity.
     * Configures security rules, authentication, and authorization for the application.
     *
     * @param http the ServerHttpSecurity object to build the SecurityWebFilterChain for
     * @return the built SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable()) //ou ServerHttpSecurity.CsrfSpec::disable
                // Configure authorization rules
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/login", "/logout").permitAll()
                        .pathMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                        .pathMatchers("/front", "/front/", "/front/**", "/patients", "/patients/**").hasAuthority("USER")
                        .anyExchange().authenticated()
                )
                // Configure form login
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler(authenticationSuccessHandler())
                        .authenticationFailureHandler(authenticationFailureHandler())
                )
                // Set the authentication manager
                //.authenticationManager(reactiveAuthenticationManager())
                // Configure HTTP Basic authentication as fallback for API clients
                .httpBasic(Customizer.withDefaults())
                // Configure logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .build();
    }
}
