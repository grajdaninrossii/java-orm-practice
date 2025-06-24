package org.stackbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stackbook.entity.UserIdJpaBuddy;

public interface UserIdJpaBuddyRepo extends JpaRepository<UserIdJpaBuddy, Long> {
}
