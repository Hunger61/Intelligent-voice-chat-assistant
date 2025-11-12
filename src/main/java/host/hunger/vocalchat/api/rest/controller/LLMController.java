package host.hunger.vocalchat.api.rest.controller;

import host.hunger.vocalchat.api.rest.annotation.SkipToken;
import host.hunger.vocalchat.api.rest.dto.AIAssistantConfigDTO;
import host.hunger.vocalchat.api.rest.dto.QuestionDTO;
import host.hunger.vocalchat.application.service.AIAssistantApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/llm")
public class LLMController {

    private final AIAssistantApplicationService aiAssistantApplicationService;

    @PostMapping("/createNewAssistant")
    public void createNewAssistant(@RequestBody AIAssistantConfigDTO aiAssistantConfigDTO){
        aiAssistantApplicationService.createNewAssistant(aiAssistantConfigDTO.getName(), aiAssistantConfigDTO.getDescription(), aiAssistantConfigDTO.getCharacter());
    }


    @PostMapping("/generateReply")
    @SkipToken
    public String generateReply(@RequestBody QuestionDTO questionDTO){
        return aiAssistantApplicationService.answerQuestion(questionDTO.getQuestion(), questionDTO.getAiAssistantId());
    }
}
