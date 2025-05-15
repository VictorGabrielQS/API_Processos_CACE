package cace.processos_api.interceptor;

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
    private final Map<Long, Boolean> downgraded = new ConcurrentHashMap<>();


    public Bucket resolveBucket(Long userId, int nivel) {
        return buckets.computeIfAbsent(userId, id -> createBucket(nivel));
    }

    private  Bucket createBucket(int nivel){
        if (nivel == 2 ){

            // Primeira etapa: 5 requisições por segundo
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(5 , Refill.greedy(5, Duration.ofSeconds(1))))
                    .build();
        }
        else {
            // Nível 1: sem limite
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(Long.MAX_VALUE, Refill.intervally(Long.MAX_VALUE, Duration.ofSeconds(1))))
                    .build();
        }
    }


    public void downgradeToSloweRate(Long userId) {
        // Troca para 1 requisição por segundo
        Bucket slowBucket = Bucket.builder()
                .addLimit(Bandwidth.classic(1, Refill.greedy(1, Duration.ofSeconds(1))))
                .build();
        buckets.put(userId, slowBucket);
        downgraded.put(userId, true);

    }


    public boolean isDowngraded(Long userId) {
        return downgraded.getOrDefault(userId, false);
    }

}
