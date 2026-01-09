package com.aifa.finance.repository;

import com.aifa.finance.domain.Goal;
import com.aifa.finance.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserAndStatus(User user, Goal.GoalStatus status);
    List<Goal> findByUser(User user);
}
