package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.event.QuestionAnsweredEvent;
import host.hunger.vocalchat.domain.model.speech.SpeechProcessor;
import host.hunger.vocalchat.domain.repository.SpeechProcessorRepository;
import host.hunger.vocalchat.domain.service.SpeechDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TextToSpeechApplicationService {

    private final SpeechDomainService speechDomainService;
    private final SpeechProcessorRepository speechProcessorRepository;

    @EventListener
    public void TextToSpeech(QuestionAnsweredEvent event)
    {
        SpeechProcessor speechProcessor = speechProcessorRepository.findById(event.getUserId());
        if(speechProcessor == null){
            return;
        }
        speechDomainService.TextToSpeech(event.getAnswer(), speechProcessor);
    }
}
