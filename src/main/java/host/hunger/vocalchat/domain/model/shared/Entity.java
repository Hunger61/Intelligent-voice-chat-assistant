package host.hunger.vocalchat.domain.model.shared;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public abstract class Entity<ID extends Identity> {
    protected ID id;
    protected Entity(ID id) {
        this.id = id;
    }
}
