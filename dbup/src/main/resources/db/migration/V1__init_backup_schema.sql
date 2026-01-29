-- =======================
-- DATABASE CONNECTIONS
-- =======================
CREATE TABLE database_connection (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    database_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);



-- =======================
-- BACKUP JOBS
-- =======================
CREATE TABLE backup_job (
    id UUID PRIMARY KEY,
    database_connection_id UUID NOT NULL,
    backup_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    schedule_time TIMESTAMP,
    created_at TIMESTAMP,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    error_message VARCHAR(4000),

    CONSTRAINT fk_backup_job_db
        FOREIGN KEY (database_connection_id)
        REFERENCES database_connection(id)
);

-- IMPORTANT INDEX
CREATE INDEX idx_backup_job_status
    ON backup_job(status);

-- =======================
-- BACKUP EXECUTION LOGS
-- =======================
CREATE TABLE backup_execution_log (
    id UUID PRIMARY KEY,
    backup_job_id UUID NOT NULL,
    message VARCHAR(2000) NOT NULL,
    timestamp TIMESTAMP NOT NULL,

    CONSTRAINT fk_backup_log_job
        FOREIGN KEY (backup_job_id)
        REFERENCES backup_job(id)
);