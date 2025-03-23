package ru.otus.core.repository.executor;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import ru.otus.core.sessionmanager.DataBaseOperationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DbExecutorImpl implements DbExecutor {

    @Override
    public long executeStatement(Connection connection, String sql, List<Object> params) {
        try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Подставляем параметры в запрос
            for (int idx = 0; idx < params.size(); idx++) {
                preparedStatement.setObject(idx + 1, params.get(idx));
            }

            // Выполняем запрос
            preparedStatement.executeUpdate();

            // Получаем сгенерированные ключи
            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1); // Возвращаем сгенерированный ключ
                } else {
                    throw new DataBaseOperationException("No generated key returned from database", null);
                }
            }
        } catch (SQLException e) {
            throw new DataBaseOperationException("Failed to execute statement: " + sql, e);
        }
    }

    @Override
    public <T> Optional<T> executeSelect(
            Connection connection, @Language("SQL") String sql, @NotNull List<Object> params, Function<ResultSet, T> rsHandler) {
        try (var pst = connection.prepareStatement(sql)) {
            for (var idx = 0; idx < params.size(); idx++) {
                pst.setObject(idx + 1, params.get(idx));
            }
            try (var rs = pst.executeQuery()) {
                return Optional.ofNullable(rsHandler.apply(rs));
            }
        } catch (SQLException ex) {
            throw new DataBaseOperationException("executeSelect error", ex);
        }
    }
}
