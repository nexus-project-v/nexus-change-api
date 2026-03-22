/*
package br.com.nexus.transaction.commons.filter;

import br.com.nexus.transaction.commons.Constants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtém o token JWT do cabeçalho Authorization
        final String authorizationHeader = request.getHeader("Authorization");
        log.info("Authorization Header: {}", authorizationHeader);
        String jwtToken = null;
        String token = null;
        String username = null;
        String email = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            // Remove "Bearer " para obter o token puro
        }

        if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Decodifica o JWT usando a biblioteca auth0 (ou outra de sua escolha)
                log.info("Decodificando token JWT: {}", jwtToken);
                DecodedJWT decodedJWT = JWT.decode(jwtToken);

                if ("access".equals(decodedJWT.getClaim("token_use").asString())) {
                    username = decodedJWT.getClaim("username").asString();
                    email = decodedJWT.getClaim("email").asString();// Extrair o username do token
                }

                if (validateToken(decodedJWT)) {
                    // Aqui você pode construir uma autenticação personalizada, se necessário
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Define a autenticação no SecurityContext para ser usada nas requisições
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // Adiciona o JWT decodificado como atributo no request
                    //request.setAttribute(Constants.BEARER_TOKEN_ATTRIBUTE, decodedJWT.getToken());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (JWTDecodeException e) {
                // Token inválido ou malformado, então a requisição falha com 401 Unauthorized
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido");
                return;
            }
        }

        // Continua com o próximo filtro na cadeia
        filterChain.doFilter(request, response);
    }

    // Método para validar o token JWT
    private boolean validateToken(DecodedJWT jwt) {
        // Verificar se o token não expirou
        return jwt.getExpiresAt().after(new java.util.Date());
    }
}
*/
