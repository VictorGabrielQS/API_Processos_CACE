package cace.processos_api.interceptor;

import cace.processos_api.model.Usuario;
import cace.processos_api.repository.UsuarioRepository;
import cace.processos_api.util.AuthUtil;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RateLimitingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

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
        String path = httpRequest.getRequestURI();

        try {
            Long userId = authUtil.getUsuarioLogado().getId();
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);


            if (usuario != null) {
                Bucket bucket = rateLimiterService.resolveBucket(usuario);

                if (bucket.tryConsume(1)) {
                    logger.info("Requisição permitida para usuário [{}] em {}", usuario.getUsername(), path);
                    chain.doFilter(request, response);
                } else {
                    logger.warn("LIMITE EXCEDIDO para usuário [{}] em {}", usuario.getUsername(), path);
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setStatus(429);
                    httpResponse.getWriter().write("Limite de requisições excedido. Tente novamente mais tarde.");
                }
            } else {
                logger.info("Usuário não autenticado acessando {}", path);
                chain.doFilter(request, response);
            }

        } catch (Exception e) {
            logger.error("Erro no filtro de rate limit: ", e);
            chain.doFilter(request, response);
        }
    }
}
