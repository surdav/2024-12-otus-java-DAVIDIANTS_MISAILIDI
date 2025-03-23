package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.jdbc.exception.OrmMappingException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            // Преобразуем имя поля в имя геттера (например, "name" -> "getName")
            String getterName = "get" + capitalizeFirstLetter(field.getName());

            // Ищем метод с именем геттера
            Method getter = object.getClass().getMethod(getterName);

            // Вызываем найденный геттер у объекта
            return getter.invoke(object);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error getting value for field: " + field.getName(), e);
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
            // Создаем новый экземпляр объекта через рефлексию
            T instance = entityClassMetaData.getConstructor().newInstance();

            // Устанавливаем значения всех полей объекта, используя сеттеры
            for (Field field : entityClassMetaData.getAllFields()) {
                // Получаем имя поля и создаем имя сеттера
                String setterName = "set" + capitalizeFirstLetter(field.getName());

                // Проверяем наличие метода сеттера
                Method setter = instance.getClass().getMethod(setterName, field.getType());

                // Получаем значение соответствующего поля из ResultSet
                Object value = rs.getObject(field.getName(), field.getType());

                // Вызываем сеттер с извлеченным значением
                setter.invoke(instance, value);
            }

            return instance;
        } catch (SQLException | ReflectiveOperationException e) {
            throw new OrmMappingException("Error while mapping ResultSet to the object", e);
        }
    }
}
