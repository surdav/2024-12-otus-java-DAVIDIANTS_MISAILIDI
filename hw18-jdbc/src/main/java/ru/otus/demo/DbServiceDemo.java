package ru.otus.demo;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.executor.DbExecutorImpl;
import ru.otus.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.crm.datasource.DriverManagerDataSource;
import ru.otus.crm.model.Client;
import ru.otus.crm.repository.ClientDataTemplateJdbc;
import ru.otus.crm.service.DbServiceClientImpl;

import javax.sql.DataSource;

public class DbServiceDemo {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static void main(String[] args) {
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

        var clientTemplate = new ClientDataTemplateJdbc(dbExecutor); // реализация DataTemplate, заточена на Client

        var dbServiceClient = new DbServiceClientImpl(transactionRunner, clientTemplate);

        // Сохранение клиента без возврата
        dbServiceClient.saveClient(new Client("dbServiceFirst"));

        // Сохранение и выбор клиента "dbServiceSecond"
        var clientSecondSelected = saveAndSelectClient(dbServiceClient, new Client("dbServiceSecond"));
        log.info("clientSecondSelected: {}", clientSecondSelected);

        // Обновление клиента и его выбор
        var clientUpdated = saveAndSelectClient(dbServiceClient, new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated"));
        log.info("clientUpdated: {}", clientUpdated);

        // Печать всех клиентов
        log.info("All clients:");
        dbServiceClient.findAll().forEach(client -> log.info("client: {}", client));
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }

    /**
     * Сохраняет клиента в базе данных и извлекает его обратно.
     *
     * @param dbServiceClient Сервис работы с клиентами
     * @param client Данные клиента для сохранения
     * @return Извлеченный клиент из БД
     */
    private static Client saveAndSelectClient(DbServiceClientImpl dbServiceClient, Client client) {
        var savedClient = dbServiceClient.saveClient(client);

        // Получение сохраненного клиента
        return dbServiceClient.getClient(savedClient.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id: " + savedClient.getId()));
    }
}