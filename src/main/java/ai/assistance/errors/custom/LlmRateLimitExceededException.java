package ai.assistance.errors.custom;

public class LlmRateLimitExceededException extends RuntimeException {

    public LlmRateLimitExceededException(String message) {
        super(message);
    }
}
