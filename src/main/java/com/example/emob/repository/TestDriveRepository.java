package com.example.emob.repository;

import com.example.emob.entity.TestDrive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestDriveRepository extends JpaRepository<TestDrive, UUID> {
}
