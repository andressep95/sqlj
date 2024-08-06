package cl.playground.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll(int page, int pageSize);
    T update(T entity);
    void deleteByID(ID id);

}
