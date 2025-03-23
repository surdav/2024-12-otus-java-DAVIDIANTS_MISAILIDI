package ru.otus.jdbc.mapper;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;

    private final EntitySQLMetaData entitySQLMetaData;

    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {

        return dbExecutor.executeSelect(
                connection,
                entitySQLMetaData.getSelectByIdSql(),
                List.of(id),
                rs -> {
                    try {
                        if (rs.next()) {
                            return createObjectFromResultSet(rs);
                        }
                        return null;
                    } catch (Exception e) {
                        throw new UnsupportedOperationException("Error creating object from result set", e);
                    }
                });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(
                connection,
                entitySQLMetaData.getSelectAllSql(),
                List.of(),
                rs -> {
                    List<T> list = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            list.add(createObjectFromResultSet(rs));
                        }
                    } catch (Exception e) {
                        throw new UnsupportedOperationException("Error creating objects list from result set", e);
                    }
                    return list;
                }).orElseThrow();
    }

    @Override
    public long insert(Connection connection, T object) {

        // Get the list of fields, excluding the ID field
        List<Object> values = getFieldsValues(entityClassMetaData.getFieldsWithoutId(), object);

        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), values);
    }

    @Override
    public void update(Connection connection, T object) {

        List<Object> values = getFieldsValues(entityClassMetaData.getFieldsWithoutId(), object);

        values.add(getFieldValue(entityClassMetaData.getIdField(), object)); // Add ID at the end

        dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), values);
    }

    private List<Object> getFieldsValues(List<Field> fields, T object) {

        List<Object> values = new ArrayList<>();

        for (Field field : fields) {
            values.add(getFieldValue(field, object));
        }

        return values;
    }

    private Object getFieldValue(Field field, T object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error getting value for field: " + field.getName(), e);
        }
    }

    private T createObjectFromResultSet(ResultSet rs) throws Exception {

        T instance = entityClassMetaData.getConstructor().newInstance();

        for (Field field : entityClassMetaData.getAllFields()) {
            field.setAccessible(true);
            field.set(instance, rs.getObject(field.getName()));
        }

        return instance;
    }
}
