package com.sky.dbup.worker.strategy;

import com.sky.dbup.domain.entity.BackupJob;
import com.sky.dbup.domain.entity.DatabaseConnection;
import com.sky.dbup.domain.enums.DatabaseType;
import com.sky.dbup.worker.BackupExecutionResult;

public interface BackupStrategy {
    boolean supports(DatabaseType databaseType);

    BackupExecutionResult executeBackup(
      DatabaseConnection connection,
      BackupJob job
    );
}
