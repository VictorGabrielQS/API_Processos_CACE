package cace.processos_api.interceptor;

import cace.processos_api.model.Usuario;
import cace.processos_api.repository.UsuarioRepository;
import cace.processos_api.util.AuthUtil;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


public class RateLimitingFilter implements Filter {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            Long userId = authUtil.getUsuarioLogado().getId(); // Pega o ID do usuário logado
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);

            if (usuario != null) {
                Bucket bucket = rateLimiterService.resolveBucket(usuario);

                if (bucket.tryConsume(1)) {
                    chain.doFilter(request, response);
                } else {
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setStatus(429); // Too Many Requests
                    httpResponse.getWriter().write("Limite de requisições excedido. Tente novamente mais tarde.");
                    return;
                }
            } else {
                chain.doFilter(request, response); // Usuário não autenticado? Deixa passar ou bloqueia como preferir
            }

        } catch (Exception e) {
            chain.doFilter(request, response); // Deixa passar em caso de erro
        }
    }
}