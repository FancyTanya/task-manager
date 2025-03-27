package org.bitbucket.privattaskmanager.datasource;

import org.bitbucket.privattaskmanager.config.DataSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import java.util.function.Supplier;

public class DatabaseOperationHandler {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseOperationHandler.class);

    private DatabaseOperationHandler(){}

    public static <T> T execute(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (DataAccessException ex) {
            logger.error("Main database failed, switching to backup", ex);
            DataSourceConfig.switchToBackup();
            return operation.get();
        }
    }
}
