package com.sky.dbup.infrastructure.repository;

import com.sky.dbup.domain.entity.DatabaseConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DatabaseConnectionRepository extends JpaRepository<DatabaseConnection, UUID> {
    Optional<DatabaseConnection> findByName(String name);
}
