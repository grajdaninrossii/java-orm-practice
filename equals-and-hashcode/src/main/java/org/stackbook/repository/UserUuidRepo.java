package org.stackbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stackbook.entity.UserUuid;

import java.util.UUID;

public interface UserUuidRepo extends JpaRepository<UserUuid, UUID> {
}
