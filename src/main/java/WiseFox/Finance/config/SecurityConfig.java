package WiseFox.Finance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. DESACTIVAR CSRF
            .csrf(csrf -> csrf.disable()) 
            
            // 2. CONFIGURAR PERMISOS:
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            )
            
            // 3. MANTENER AUTH BÁSICA (opcional, para usar el user/pass de Postman)
            .httpBasic(withDefaults());

        return http.build();
    }
}