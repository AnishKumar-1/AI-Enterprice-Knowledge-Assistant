package ai.assistance.configuration.AiModelConfig;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ModelConfig {

    @Bean
    @Primary
    public EmbeddingModel ollamaModel(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        return embeddingModel;
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }

//    @Bean
//    public ChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate){
//        return JdbcChatMemoryRepository.builder()
//                .jdbcTemplate(jdbcTemplate)
//                .build();
//    }

}
