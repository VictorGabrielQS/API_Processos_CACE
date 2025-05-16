package cace.processos_api.interceptor;

import cace.processos_api.model.Usuario;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
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
        if (usuario.getNivelAcesso() == 1) {
            // 1000 req/hora
            Bandwidth limit = Bandwidth.classic(1000, Refill.greedy(1000, Duration.ofHours(1)));
            return Bucket.builder().addLimit(limit).build();
        } else if (usuario.getNivelAcesso() == 2) {
            // 5 req/segundo e 1 req/segundo (limitadores compostos)
            Bandwidth fastLimit = Bandwidth.simple(5, Duration.ofSeconds(1));
            Bandwidth slowLimit = Bandwidth.simple(1, Duration.ofSeconds(1)).withInitialTokens(1);
            return Bucket.builder().addLimit(fastLimit).addLimit(slowLimit).build();
        } else {
            // Bloqueia tudo
            return Bucket.builder().addLimit(Bandwidth.simple(0, Duration.ofSeconds(1))).build();
        }
    }

}