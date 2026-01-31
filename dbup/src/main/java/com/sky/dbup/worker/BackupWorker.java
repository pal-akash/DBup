package com.sky.dbup.worker;

import com.sky.dbup.domain.entity.BackupExecutionLog;
import com.sky.dbup.domain.entity.BackupJob;
import com.sky.dbup.domain.enums.BackupStatus;
import com.sky.dbup.infrastructure.repository.BackupExecutionLogRepository;
import com.sky.dbup.infrastructure.repository.BackupJobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackupWorker {

    private final BackupJobRepository backupJobRepository;
    private final BackupExecutionLogRepository logRepository;
    private final BackupExecutor  backupExecutor;

    @Transactional
    public Optional<BackupJob> claimNextJob(){
        Pageable limitOne = PageRequest.of(0,1);

        List<BackupJob> jobs = backupJobRepository.findNextJobForUpdate(
                BackupStatus.PENDING,
                limitOne
        );

        if(jobs.isEmpty()){
            return Optional.empty();
        }

        BackupJob job = jobs.getFirst();
        job.setBackupStatus(BackupStatus.RUNNING);
        job.setStartedAt(LocalDateTime.now());

        return Optional.of(job);
    }

    public void executeNextJob() {
        Optional<BackupJob> optionalJob = claimNextJob();

        if(optionalJob.isEmpty()){
            return;
        }

        BackupJob job = optionalJob.get();
        log.info("Executing backup job {}", job.getId());

        try{
            BackupExecutionResult  result = backupExecutor.execute(job);

            if(result.isSuccess()){
                job.setBackupStatus(BackupStatus.COMPLETED);
                job.setFinishedAt(LocalDateTime.now());
                saveLog(job, "Backup completed successfully");
            } else {
                job.setBackupStatus(BackupStatus.FAILED);
                job.setErrorMessage(result.getErrorMessage());
                job.setFinishedAt(LocalDateTime.now());
                saveLog(job, "Backup failed: " + result.getErrorMessage());
            }

        } catch (Exception ex) {
            job.setBackupStatus(BackupStatus.FAILED);
            job.setErrorMessage(ex.getMessage());
            job.setFinishedAt(LocalDateTime.now());
            saveLog(job, "Unexpected error: " + ex.getMessage());
        }

        backupJobRepository.save(job);
    }

    private void saveLog(BackupJob job, String message){
        logRepository.save(
                BackupExecutionLog.builder()
                        .backupJob(job)
                        .message(message)
                        .timeStamp(LocalDateTime.now())
                .build()
        );
    }
}
