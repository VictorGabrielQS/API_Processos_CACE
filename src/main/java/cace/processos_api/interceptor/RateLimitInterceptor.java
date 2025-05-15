package cace.processos_api.interceptor;

import cace.processos_api.model.Usuario;
import cace.processos_api.repository.UsuarioRepository;
import cace.processos_api.util.AuthUtil;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final UsuarioRepository usuarioRepository;

    public RateLimitInterceptor(RateLimiterService rateLimiterService, UsuarioRepository usuarioRepository) {
        this.rateLimiterService = rateLimiterService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId = AuthUtil.getUsuarioLogado().getId();
        Usuario usuario = usuarioRepository.findById(userId).orElse(null);

        if (usuario == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado");
            return false;
        }

        int nivel = usuario.getNivelAcesso();

        if (nivel == 1) {
            return true; // Sem limite
        }

        Bucket bucket = rateLimiterService.resolveBucket(userId, nivel);
        if (bucket.tryConsume(1)) {
            return true;
        } else {
            if (!rateLimiterService.isDowngraded(userId)) {
                rateLimiterService.downgradeToSloweRate(userId);
            }
            response.setStatus(429);
            response.getWriter().write("Limite de requisições excedido. Aguardando...");
            return false;
        }
    }
}