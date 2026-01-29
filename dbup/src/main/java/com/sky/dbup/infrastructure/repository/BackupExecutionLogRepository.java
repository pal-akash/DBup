package com.sky.dbup.infrastructure.repository;

import com.sky.dbup.domain.entity.BackupExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BackupExecutionLogRepository extends JpaRepository<BackupExecutionLog, UUID> {
}
