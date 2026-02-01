package com.sky.dbup.worker.strategy;

import com.sky.dbup.domain.entity.BackupJob;
import com.sky.dbup.domain.entity.DatabaseConnection;
import com.sky.dbup.domain.enums.DatabaseType;
import com.sky.dbup.infrastructure.storage.BackupCompressionService;
import com.sky.dbup.infrastructure.storage.BackupRetentionService;
import com.sky.dbup.worker.BackupExecutionResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@AllArgsConstructor
public class PostgresBackupStrategy implements BackupStrategy{

    private final BackupCompressionService compressionService;
    private final BackupRetentionService retentionService;

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

            Path zipFile = compressionService.compressToZip(outputFile);
            retentionService.enforceRetention(outputDir);
            return new BackupExecutionResult(true, null, zipFile);
        } catch (Exception e) {
            return new BackupExecutionResult(false, e.getMessage(), null);
        }
    }
}
