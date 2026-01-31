package com.sky.dbup.infrastructure.repository;

import com.sky.dbup.domain.entity.BackupJob;
import com.sky.dbup.domain.enums.BackupStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BackupJobRepository extends JpaRepository<BackupJob, UUID> {
    List<BackupJob> findByStatusOrderByCreatedAtAsc(BackupStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
SELECT bj FROM BackupJob bj
WHERE bj.status = :status
ORDER BY bj.createdAt ASC
"""
    )
    List<BackupJob> findNextJobForUpdate(
            @Param("status") BackupStatus status,
            Pageable pageable
    );
}
