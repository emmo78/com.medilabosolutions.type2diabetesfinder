package com.medilabosolutions.type2diabetesfinder.gatewayservice.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

/**
 * Class for security configuration of the application.
 * filter chain, login/logout configurations, and OAuth2 login configurations.
 *
 * @author olivier morel
 */
@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/*    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("user"))
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    } */

    /**
     * sets up the authentication manager bean for reactive applications
     *
     * @return an instance of {@link ReactiveAuthenticationManager}
     */
/*    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
            new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        manager.setPasswordEncoder(passwordEncoder());
        return manager;
    }
*/
    /**
     * Fournit un gestionnaire d'authentification pour vérifier les informations d'identification
     * de l'utilisateur test-user.
     *
     * @return une instance de ReactiveAuthenticationManager pour l'authentification
     */
    @Bean
    public ReactiveAuthenticationManager testUserAuthenticationManager() {
        return authentication -> {
            final String name = authentication.getName();
            final String password = authentication.getCredentials().toString();
            if (("user".equals(name) && "user".equals(password))) {
                return Mono.just(new UsernamePasswordAuthenticationToken(name, password, Collections.singletonList(new SimpleGrantedAuthority("USER"))
                ));
            }
            return Mono.empty(); // Retourner Mono.empty() au lieu de null pour respecter les principes réactifs
        };
    }


    /**
     * builds a SecurityFilterChain bean for the provided HttpSecurity.
     *
     * @param http the HttpSecurity object to build the SecurityFilterChain for
     * @return the built SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)  // Généralement désactivé pour les API Gateway
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/front", "/front/", "/front/home").permitAll()
                        .pathMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                        .pathMatchers("/front/**", "/patients", "/patients/**").hasAuthority("USER")
                        .anyExchange().authenticated())
                .formLogin(form -> form
                        //.loginPage("/login")
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            webFilterExchange.getExchange().getResponse()
                                    .setStatusCode(org.springframework.http.HttpStatus.FOUND);
                            webFilterExchange.getExchange().getResponse()
                                    .getHeaders().setLocation(URI.create("/front/home"));
                            return Mono.empty();
                        }))
                .httpBasic(Customizer.withDefaults())
                /*.logout(logout -> logout
                        .logoutUrl("/app-logout"))*/
                .build();
    }
}