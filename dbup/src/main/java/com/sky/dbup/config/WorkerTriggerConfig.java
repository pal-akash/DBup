package com.sky.dbup.config;

import com.sky.dbup.worker.BackupWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class WorkerTriggerConfig {

    private final BackupWorker  backupWorker;

    @Scheduled(fixedDelay = 5000)
    public void runWorker() {
        backupWorker.executeNextJob();
    }
}
