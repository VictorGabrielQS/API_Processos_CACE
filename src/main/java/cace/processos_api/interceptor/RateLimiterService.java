package cace.processos_api.interceptor;

import cace.processos_api.model.process.Usuario;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterService {

    private final Map<Long, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(Usuario usuario) {
        return buckets.computeIfAbsent(usuario.getId(), id -> createBucket(usuario));
    }

    private Bucket createBucket(Usuario usuario) {
        switch (usuario.getNivelAcesso()) {
            case 1:
                // Sem limites para o nível 1 - retorna null para indicar isso
                return null;
            case 2:
                // 5 req/segundo e 1 req/segundo (limitadores compostos)
                Bandwidth fastLimit = Bandwidth.simple(5, Duration.ofSeconds(1));
                Bandwidth slowLimit = Bandwidth.simple(1, Duration.ofSeconds(1)).withInitialTokens(1);
                return Bucket.builder().addLimit(fastLimit).addLimit(slowLimit).build();
            default:
                // Bloqueia todas as requisições para outros níveis
                return Bucket.builder().addLimit(Bandwidth.simple(0, Duration.ofSeconds(1))).build();
        }
    }
}
