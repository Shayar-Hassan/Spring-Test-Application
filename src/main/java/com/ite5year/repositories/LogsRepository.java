package com.ite5year.repositories;

import com.ite5year.models.ApplicationUser;
import com.ite5year.models.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepository extends JpaRepository<Logs, Long> {
}
