package org.stackbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stackbook.entity.UserId;

public interface UserIdRepo extends JpaRepository<UserId, Long> {
}
