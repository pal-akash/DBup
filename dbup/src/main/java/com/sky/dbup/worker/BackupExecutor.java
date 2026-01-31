package com.sky.dbup.worker;

import com.sky.dbup.domain.entity.BackupJob;
import com.sky.dbup.domain.entity.DatabaseConnection;
import com.sky.dbup.worker.strategy.BackupStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BackupExecutor {

    private final List<BackupStrategy> strategies;

    public BackupExecutionResult execute(BackupJob job){
        DatabaseConnection connection = job.getDatabaseConnection();

        BackupStrategy strategy = strategies.stream()
                .filter(s -> s.supports(connection.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No backup strategy for " + connection.getType()
                )
                );

        return strategy.executeBackup(connection, job);
    }
}
