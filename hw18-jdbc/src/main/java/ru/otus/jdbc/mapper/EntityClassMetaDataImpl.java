package ru.otus.jdbc.mapper;

import ru.otus.annotation.Id;
import ru.otus.jdbc.exception.NoSuitableConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> clazz;
    private final String className;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;

        // Именование класса
        this.className = clazz.getSimpleName();

        // Кэшируем конструктор
        this.constructor = findConstructor();

        // Кэшируем поле с аннотацией @Id
        this.idField = findIdField();

        // Кэшируем все поля
        this.allFields = List.of(clazz.getDeclaredFields());

        // Кэшируем все поля без @Id
        this.fieldsWithoutId = this.allFields.stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toList();
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }

    // Вспомогательные методы для поиска конструктора и поля @Id
    private Constructor<T> findConstructor() {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new NoSuitableConstructorException("Default constructor not found for class: " + clazz.getName(), e);
        }
    }

    private Field findIdField() {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No @Id field found in class: " + clazz.getName()));
    }
}