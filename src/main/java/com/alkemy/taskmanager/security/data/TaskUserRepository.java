package com.alkemy.taskmanager.security.data;


import com.alkemy.taskmanager.security.domain.TaskUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskUserRepository extends JpaRepository<TaskUser, Long> {
    Optional<TaskUser> findByUsername(String username);
}
