package ru.otus.core.repository;

import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface DataTemplate<T> {

    Optional<T> findById(Connection connection, long id);

    List<T> findAll(Connection connection);

    void update(Connection connection, T object);

    long insert(Connection connection, Client client);

    long insert(Connection connection, Manager manager);
}
