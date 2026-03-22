package br.com.nexus.change.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // ajuste se for usar cookies; se usar cookies, deve ser true
        // se o front usa `withCredentials = true` a resposta precisa conter Access-Control-Allow-Credentials: true
        cfg.setAllowCredentials(false);

        // Permita seus fronts locais de desenvolvimento
        cfg.setAllowedOriginPatterns(List.of("*"));

        // alternativa: use allowed origin patterns (útil quando a porta pode mudar)
        // cfg.setAllowedOriginPatterns(List.of("http://localhost:*"));

        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of(
                "Content-Type",
                "Authorization",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));
        // se quiser expor algo:
        cfg.setExposedHeaders(List.of("Authorization"));

        // tempo que o preflight fica em cache (opcional)
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // mapeie todo o contexto (lembre: seu context-path é /api)
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

}
