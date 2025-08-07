package host.hunger.vocalchat.domain.model.speech;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Session extends ValueObject {
    private final Object session;
}
