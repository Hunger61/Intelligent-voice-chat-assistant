package host.hunger.vocalchat.api.rest.controller;

import host.hunger.vocalchat.api.rest.dto.QuestionDTO;
import host.hunger.vocalchat.application.service.AIAssistantApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/llm")
public class LLMController {

    private final AIAssistantApplicationService aiAssistantApplicationService;

    @PostMapping("/configureLLM")
    public String ConfigureLLM(){
        return "LLM configured";
    }//todo

    @PostMapping("/generateReply")
    public String generateReply(@RequestBody QuestionDTO questionDTO){
        aiAssistantApplicationService.answerQuestion(questionDTO.getQuestion(), questionDTO.getAiAssistantId());
        return "Reply generated";// todo
    }
}
