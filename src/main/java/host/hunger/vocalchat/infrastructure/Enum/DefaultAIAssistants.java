package host.hunger.vocalchat.infrastructure.Enum;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantCharacter;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantDescription;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DefaultAIAssistants {

    XIAO_ZHI(
            new AIAssistantName("小智"),
            new AIAssistantDescription("一个功能全面的AI助手，能够回答各种问题并提供一般性帮助。"),
            new AIAssistantCharacter("你是一位乐于助人且友好的助手。回答问题时要清晰且简洁。")
    );

    private final AIAssistantName name;
    private final AIAssistantDescription description;
    private final AIAssistantCharacter character;
}
