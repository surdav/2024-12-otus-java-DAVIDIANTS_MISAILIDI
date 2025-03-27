package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;
import ru.otus.jdbc.exception.OrmMappingException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public long insert(Connection connection, Client client) {
        // SQL для вставки клиента
        String sql = "INSERT INTO client(name) VALUES (?) RETURNING id";

        // Передаём только name, без id
        long id = dbExecutor.executeStatement(connection, sql, Collections.singletonList(client.getName()));

        // Устанавливаем сгенерированный id клиенту
        client.setId(id);
        return id;
    }

    @Override
    public long insert(Connection connection, Manager manager) {
        // SQL для вставки менеджера
        String sql = "INSERT INTO manager(label, param1) VALUES (?, ?) RETURNING no";

        // Передаём данные label и param1, без no
        long no = dbExecutor.executeStatement(connection, sql, List.of(manager.getLabel(), manager.getParam1()));

        // Устанавливаем сгенерированный no менеджеру
        manager.setNo(no);
        return no;
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
            String getterName;
            if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                // Для boolean используем "is"
                getterName = "is" + capitalizeFirstLetter(field.getName());
            } else {
                // Для остальных типов используем "get"
                getterName = "get" + capitalizeFirstLetter(field.getName());
            }

            Method getter = object.getClass().getMethod(getterName);
            return getter.invoke(object);
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    "Error getting value for field: " + field.getName(), e
            );
        }
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private T createObjectFromResultSet(ResultSet rs) throws OrmMappingException {
        try {
            T instance = entityClassMetaData.getConstructor().newInstance();

            // Устанавливаем значения полей
            for (Field field : entityClassMetaData.getAllFields()) {
                field.setAccessible(true); // Даем доступ к private/protected полю

                // Получаем значение поля из ResultSet
                Object value = rs.getObject(field.getName(), field.getType());

                // Устанавливаем значение в объект через Field API
                field.set(instance, value);
            }

            return instance;
        } catch (SQLException | ReflectiveOperationException e) {
            throw new OrmMappingException("Error while mapping ResultSet to the object", e);
        }
    }
}
