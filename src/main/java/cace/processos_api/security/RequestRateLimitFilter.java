package cace.processos_api.security;

import cace.processos_api.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RequestRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Integer> requestCounter = new ConcurrentHashMap<>();
    private static final int TOO_MANY_REQUESTS = 429;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Se ainda não autenticado, continua o fluxo
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario)) {
            filterChain.doFilter(request, response);
            return;
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String username = usuario.getUsername();
        int nivel = usuario.getNivelAcesso();

        // Define limite de requisições por nível
        int limite = switch (nivel) {
            case 2 -> 1000;
            case 1 -> 100;
            default -> 50;
        };

        int count = requestCounter.getOrDefault(username, 0);

        if (count >= limite) {
            response.setStatus(TOO_MANY_REQUESTS);
            response.getWriter().write("Limite de requisições atingido para seu nível de acesso.");
            return;
        }

        requestCounter.put(username, count + 1);
        filterChain.doFilter(request, response);
    }
}
