package host.hunger.vocalchat.domain.repository;

import java.util.List;

public interface Repository<T, ID> {
    T findById(ID id);
    List<T> findAll();
    void delete(ID id);
    void save(T entity);
    boolean exists(ID id);
}
