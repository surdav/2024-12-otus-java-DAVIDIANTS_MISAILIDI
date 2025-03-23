package ru.otus;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.executor.DbExecutorImpl;
import ru.otus.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.crm.datasource.DriverManagerDataSource;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.crm.service.DbServiceManagerImpl;
import ru.otus.jdbc.mapper.*;

@SuppressWarnings({"java:S125", "java:S1481"})
public class HomeWork {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        log.info("Hello, Otus!");

        // Общая часть
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);

        flywayMigrations(dataSource);

        var transactionRunner = new TransactionRunnerJdbc(dataSource);

        var dbExecutor = new DbExecutorImpl();

        // Используем новый метод для создания dbServiceClient
        var dbServiceClient = createDbServiceClient(transactionRunner, dbExecutor);

        dbServiceClient.saveClient(new Client("dbServiceFirst"));

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));

        var clientSecondSelected = dbServiceClient
                .getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));

        log.info("clientSecondSelected:{}", clientSecondSelected);

        // Работа с Manager
        EntityClassMetaData<Manager> entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);

        EntitySQLMetaData entitySQLMetaDataManager = new EntitySQLMetaDataImpl(entityClassMetaDataManager);

        var dataTemplateManager = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataManager, entityClassMetaDataManager);

        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);

        dbServiceManager.saveManager(new Manager("ManagerFirst"));

        var managerSecond = dbServiceManager.saveManager(new Manager("ManagerSecond"));

        var managerSecondSelected = dbServiceManager
                .getManager(managerSecond.getNo())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getNo()));

        log.info("managerSecondSelected:{}", managerSecondSelected);
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");

        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();

//        flyway.clean(); // Удаляет всю существующую схему
        flyway.migrate(); // Выполняет SQL-миграции для восстановления базы данных

        log.info("db migration finished.");
        log.info("***********************************");
        log.info("      DATABASE MIGRATION DONE     ");
        log.info("***********************************");

        var result = flyway.migrate();
        log.info("Number of migrations applied: {}", result.migrations.size());
    }

    /**
     * Метод для создания `DbServiceClientImpl` с настройкой зависимостей.
     *
     * @param transactionRunner Объект для управления транзакциями
     * @param dbExecutor        Реализация DbExecutor
     * @return Настроенный экземпляр DbServiceClientImpl
     */
    private static DbServiceClientImpl createDbServiceClient(TransactionRunnerJdbc transactionRunner, DbExecutorImpl dbExecutor) {
        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl(entityClassMetaDataClient);

        var dataTemplateClient = new DataTemplateJdbc<>(
                dbExecutor, entitySQLMetaDataClient, entityClassMetaDataClient); // Реализация DataTemplate, универсальная

        return new DbServiceClientImpl(transactionRunner, dataTemplateClient);
    }
}