package cl.playground.querys;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

public class QueryGenerator<T> {

    private final Class<T> modelClass;
    private final String tableName;
    private final T entity;

    public QueryGenerator(T entity) {
        this.modelClass = (Class<T>) entity.getClass();
        this.tableName = modelClass.getAnnotation(Table.class).name();
        this.entity = entity;
    }

    public QueryGenerator(Class<T> modelClass) {
        this.entity = null;
        this.modelClass = modelClass;
        this.tableName = modelClass.getAnnotation(Table.class).name();
    }

    private void validateTableAnnotation() {
        if (modelClass.getAnnotation(Table.class) == null) {
            throw new IllegalArgumentException("Class must be annotated with @Table");
        }
    }

    public String insertIntoQuery() throws IllegalAccessException {
        validateTableAnnotation();
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner values = new StringJoiner(", ", "(", ")");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Field field : modelClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Column.class)) {
                columns.add(field.getAnnotation(Column.class).name());
                Object value = field.get(entity);
                if (value instanceof Date) {
                    values.add("'" + dateFormat.format((Date) value) + "'");
                } else {
                    values.add("'" + value.toString() + "'");
                }
            }
        }

        return "INSERT INTO " + tableName + " (" + columns.toString() + ") VALUES " + values.toString() + " RETURNING id";
    }

    public String selectByIDQuery(Integer id) {
        validateTableAnnotation();
        String idColumn = "id";

        return "SELECT * FROM " + tableName + " WHERE " + idColumn + " = " + id;
    }

    public String selectAllQuery(int page, int pageSize) {
        validateTableAnnotation();
        int offset = (page - 1) * pageSize;  // Calcular correctamente el offset

        return "SELECT * FROM " + tableName + " LIMIT " + pageSize + " OFFSET " + offset;
    }

    public String updateForIDQuery(Integer id) throws IllegalAccessException {
        validateTableAnnotation();
        StringJoiner setClause = new StringJoiner(", ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Field field : modelClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Column.class)) {
                Object value = field.get(entity);
                if (value instanceof Date) {
                    setClause.add(field.getAnnotation(Column.class).name() + " = '" + dateFormat.format((Date) value) + "'");
                } else {
                    setClause.add(field.getAnnotation(Column.class).name() + " = '" + value.toString() + "'");
                }
            }
        }

        return "UPDATE " + tableName + " SET " + setClause.toString() + " WHERE id = " + id;
    }

    public String deleteByIDQuery(Integer id) {
        validateTableAnnotation();
        String idColumn = "id";

        return "DELETE FROM " + tableName + " WHERE " + idColumn + " = " + id;
    }
}
