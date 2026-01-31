package com.sky.dbup.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@AllArgsConstructor
public class BackupExecutionResult {
    private boolean success;
    private String errorMessage;
    private Path backupFilePath;
}
