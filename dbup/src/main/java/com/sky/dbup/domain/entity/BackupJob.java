package com.sky.dbup.domain.entity;

import com.sky.dbup.domain.enums.BackupStatus;
import com.sky.dbup.domain.enums.BackupType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "backup_job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupJob {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_connection_id",  nullable = false)
    private DatabaseConnection databaseConnection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackupType  backupType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackupStatus backupStatus;

    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @Column(length = 4000)
    private String errorMessage;
}
