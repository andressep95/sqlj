package cl.playground.repository;

import cl.playground.querys.QueryGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

@Transactional
public abstract class GenericRepositoryImpl<T, ID> implements GenericRepository<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;
    private final Class<T> entityClass;
    private final Field idField;

    @SuppressWarnings("unchecked")
    public GenericRepositoryImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.idField = findIdField();
        this.idField.setAccessible(true);
    }
    
    private Field findIdField() {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        throw new IllegalStateException("No field annotated with @Id found in class " + entityClass.getName());
    }

    @Override
    public T save(T entity) {
        QueryGenerator<T> queryGenerator = new QueryGenerator<>(entity);
        try {
            String query = queryGenerator.insertIntoQuery();
            Integer generatedId = (Integer) entityManager.createNativeQuery(query).getSingleResult();
            idField.set(entity, generatedId);
            entityManager.flush();
        } catch (IllegalAccessException e) {
            throw new InvalidDataAccessResourceUsageException("Error setting generated ID", e);
        } catch (DataAccessException e) {
            throw new InvalidDataAccessResourceUsageException("Error executing insert query", e);
        }
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        QueryGenerator<T> queryGenerator = new QueryGenerator<>(entityClass);
        String query = queryGenerator.selectByIDQuery((Integer) id);
        try {
            List<T> results = entityManager.createNativeQuery(query, entityClass).getResultList();
            if (results.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(results.get(0));
            }
        } catch (DataAccessException e) {
            throw new InvalidDataAccessResourceUsageException("Error executing find by ID query", e);
        }
    }

    @Override
    public List<T> findAll(int page, int pageSize) {
        QueryGenerator<T> queryGenerator = new QueryGenerator<>(entityClass);
        String query = queryGenerator.selectAllQuery(page, pageSize);
        try {
            return entityManager.createNativeQuery(query, entityClass).getResultList();
        } catch (DataAccessException e) {
            throw new InvalidDataAccessResourceUsageException("Error executing select query", e);
        }
    }

    @Override
    public T update(T entity) {
        QueryGenerator<T> queryGenerator = new QueryGenerator<>(entity);
        try {
            Integer id = (Integer) idField.get(entity);
            String query = queryGenerator.updateForIDQuery(id);
            entityManager.createNativeQuery(query).executeUpdate();
        } catch (IllegalAccessException e) {
            throw new InvalidDataAccessResourceUsageException("Error accessing ID field", e);
        } catch (DataAccessException e) {
            throw new InvalidDataAccessResourceUsageException("Error executing update query", e);
        }
        return entity;
    }

    @Override
    public void deleteByID(ID id) {
        QueryGenerator<T> queryGenerator = new QueryGenerator<>(entityClass);
        String query = queryGenerator.deleteByIDQuery((Integer) id);
        try {
            entityManager.createNativeQuery(query).executeUpdate();
        } catch (DataAccessException e) {
            throw new InvalidDataAccessResourceUsageException("Error executing delete query", e);
        }
    }


}
