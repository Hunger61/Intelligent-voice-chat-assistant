package host.hunger.vocalchat.domain.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(ID id);
    void save(T entity);
    boolean exists(ID id);
}
