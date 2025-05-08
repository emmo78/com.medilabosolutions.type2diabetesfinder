package com.medilabosolutions.type2diabetesfinder.gatewayservice.service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // Implémentez votre logique pour récupérer l'utilisateur depuis votre source de données
        // Exemple simple avec un utilisateur codé en dur :
        if ("user".equals(username)) {
            return Mono.just(org.springframework.security.core.userdetails.User
                    .withUsername("user")
                    .password(passwordEncoder.encode("user"))
                    .authorities("USER")
                    .build());
        }
        return Mono.error(new UsernameNotFoundException("L'utilisateur " + username + " n'a pas été trouvé"));
    }
}