package com.project.tutor.repository;

import com.project.tutor.model.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, Integer> {
}
