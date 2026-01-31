package com.sky.dbup.worker.strategy;

import com.sky.dbup.domain.entity.BackupJob;
import com.sky.dbup.domain.entity.DatabaseConnection;
import com.sky.dbup.domain.enums.DatabaseType;
import com.sky.dbup.worker.BackupExecutionResult;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PostgresBackupStrategy implements BackupStrategy{

    @Override
    public boolean supports(DatabaseType databaseType) {
        return databaseType == DatabaseType.POSTGRES;
    }

    public BackupExecutionResult executeBackup(DatabaseConnection connection, BackupJob job){
        try{
            String fileName = connection.getName() + "-" + System.currentTimeMillis() + ".sql";
            Path outputDir = Paths.get("backups", connection.getName());
            Files.createDirectories(outputDir);

            Path outputFile = outputDir.resolve(fileName);

            ProcessBuilder builder = new ProcessBuilder(
                    "pg_dump",
                    "-h", connection.getHost(),
                    "-p", String.valueOf(connection.getPort()),
                    "-U", connection.getUsername(),
                    "-f", outputFile.toString(),
                    connection.getDatabaseName()
            );

            builder.environment().put("PGPASSWORD", connection.getPassword());
            builder.redirectErrorStream(true);

            Process process = builder.start();
            int exitCode = process.waitFor();

            if(exitCode != 0){
                return new BackupExecutionResult(
                        false,
                        "pg_dump failed with exit code" + exitCode,
                        null
                );
            }

            return new BackupExecutionResult(true, null, outputFile);
        } catch (Exception e) {
            return new BackupExecutionResult(false, e.getMessage(), null);
        }
    }
}
