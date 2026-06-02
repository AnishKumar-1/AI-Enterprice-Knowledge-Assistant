package ai.assistance.services.aiService;

import ai.assistance.Enums.MessageRole;
import ai.assistance.dtos.aiDto.AiRequest;
import ai.assistance.dtos.aiDto.AiResponseDto;
import ai.assistance.models.ChatMessage;
import ai.assistance.models.CustomDocument;
import ai.assistance.repositories.ChatMessageRepository;
import ai.assistance.repositories.DocumentRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static ai.assistance.helpers.FileHelper.extractTextFromFile;
import static ai.assistance.helpers.FileHelper.fileHash;

@Service
@Slf4j
public class DocumentReaderService {

    private final VectorStore vectorStore;
    private final DocumentRepo documentRepo;
    private final JdbcTemplate jdbcTemplate;
    private final ChatClient chatClient;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemory chatMemory;

    public DocumentReaderService(VectorStore vectorStore,
                                 DocumentRepo documentRepo, JdbcTemplate jdbcTemplate,
                                 @Qualifier("openAiChatModel") ChatModel chatModel,
                                 ChatMessageRepository chatMessageRepository,
                                 ChatMemory chatMemory) {

        this.vectorStore = vectorStore;
        this.documentRepo = documentRepo;
        this.jdbcTemplate = jdbcTemplate;
        this.chatClient = ChatClient.create(chatModel);
        this.chatMessageRepository = chatMessageRepository;
        this.chatMemory = chatMemory;
    }


    // read and store document
    @Transactional
    public String documentUpload(MultipartFile file,
                                 String category) throws Exception {

        String fileName = file.getOriginalFilename();
        if (fileName == null ||
                !fileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException(
                    "Only PDF files allowed"
            );
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Max file size is 10MB"
            );
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Please choose category");
        }

        // generate file hash
        String fileHash = fileHash(file);

        // check duplicate document
        Optional<CustomDocument> existingDocument =
                documentRepo.findByFileHash(fileHash);

        // if same document exists -> delete old vectors + row
        if (existingDocument.isPresent()) {

            Long oldDocumentId = existingDocument.get().getId();

            // delete vectors/chunks
            jdbcTemplate.update("""
                            DELETE FROM vector_store
                            WHERE metadata->>'documentId' = ?
                            """,
                    String.valueOf(oldDocumentId)
            );

            // delete relational row
            documentRepo.deleteById(oldDocumentId);
            documentRepo.flush();
        }

        // logged-in user
        String userEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // save business document
        CustomDocument customDocument =
                CustomDocument.builder()
                        .fileName(file.getOriginalFilename())
                        .category(category)
                        .uploadedBy(userEmail)
                        .fileHash(fileHash)
                        .build();

        CustomDocument savedDocument =
                documentRepo.save(customDocument);

        // extract text
        String text = extractTextFromFile(file);

        // clean text
        String cleanText =
                text.replaceAll("\\s+", " ").trim();

        log.info("Text length = {}", cleanText.length());
        // spring ai document
        Document document = new Document(cleanText);

        // metadata
        document.getMetadata().put(
                "documentId",
                savedDocument.getId()
        );

        document.getMetadata().put(
                "category",
                category
        );

        document.getMetadata().put(
                "fileName",
                file.getOriginalFilename()
        );

        document.getMetadata().put(
                "uploadedBy",
                userEmail
        );

        // chunking
        TokenTextSplitter splitter =
                new TokenTextSplitter(
                        150,
                        30,   // overlap
                        10,    // minimum chunk size
                        5000,  // max chunks
                        true   // keep separators
                );

        List<Document> chunks =
                splitter.apply(List.of(document));
        vectorStore.add(chunks);

        return "Document stored successfully";
    }


    //take user question and extract related chunks from db and then give to ai and get ai response and give to user before ai give create a prmpt
    // and user question to prompt



    public AiResponseDto ask_question(AiRequest userQuestion) {

        String question = userQuestion.getQuery();

        UUID conversationId = userQuestion.getConversationId() != null
                ? userQuestion.getConversationId()
                : UUID.randomUUID();

        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(5)
                .similarityThreshold(0.30)
                .build();

        List<Document> chunks = vectorStore.similaritySearch(searchRequest);

        String context = chunks.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"))
                .replaceAll("\\s+", " ")
                .trim();

        String systemPrompt = """
            You are an internal document assistant.

            Use conversation history when the user refers to previous messages.

            Use retrieved context when the user asks about documents.

            If the answer is available in either conversation history
            or retrieved context, answer it.

            If the answer cannot be found in either place, reply:

            "This is not present in the internal document."
            """;

        String userPrompt = """
            Context:
            %s

            Question:
            %s
            """.formatted(context, question);

        String aiResult = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(conversationId.toString())
                                .build()
                )
                .call()
                .content();

        return new AiResponseDto(
                conversationId,
                aiResult
        );
    }

}