package ru.otus.jdbc.mapper;

import ru.otus.annotation.Id;
import ru.otus.jdbc.exception.NoSuitableConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> clazz;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            // Ищем конструктор с аннотацией @Id или дефолтный
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new NoSuitableConstructorException("Default constructor not found for class: " + clazz.getName(), e);
        }
    }

    @Override
    public Field getIdField() {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class)) // Ищем поле с аннотацией @Id
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No @Id field found in class: " + clazz.getName()));
    }

    @Override
    public List<Field> getAllFields() {
        return Arrays.asList(clazz.getDeclaredFields());
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Id.class)) // Ищем все поля, кроме ID
                .toList();
    }
}