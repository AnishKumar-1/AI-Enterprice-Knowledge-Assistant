package ai.assistance.controllers;

import ai.assistance.dtos.aiDto.AiRequest;
import ai.assistance.dtos.aiDto.AiResponseDto;
import ai.assistance.services.aiService.DocumentReaderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DocumentUploadController {

    private final DocumentReaderService documentReaderService;

    public DocumentUploadController(DocumentReaderService documentReaderService) {
        this.documentReaderService = documentReaderService;
    }

    //upload company pdf document
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> uploadPDF(@RequestParam MultipartFile file, @RequestParam String category) throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("status", documentReaderService.documentUpload(file, category));
        return response;
    }

    //ask question to ai from internal document
    @GetMapping("/ask")
    public ResponseEntity<AiResponseDto> ask_to_ai(@Valid @RequestBody AiRequest userQuestionDto){
        return ResponseEntity.status(HttpStatus.OK).body(documentReaderService.ask_question(userQuestionDto));
    }

}
