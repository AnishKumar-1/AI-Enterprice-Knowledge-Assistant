package ai.assistance.services.aiService;

import ai.assistance.errors.custom.LlmRateLimitExceededException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, List<Long>> requests = new ConcurrentHashMap<>();

    public void validate(String userEmail) {

        long now = System.currentTimeMillis();

        requests.putIfAbsent(userEmail, new ArrayList<>());

        List<Long> timestamps = requests.get(userEmail);

        timestamps.removeIf(t -> now - t > 60_000);

        if (timestamps.size() >= 20) {
            throw new LlmRateLimitExceededException(
                    "LLM usage limit exceeded. Try again after 1 minute."
            );
        }

        timestamps.add(now);
    }
}
