package com.promptflow.repository;

import com.promptflow.entity.SkillPrompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillPromptRepository extends JpaRepository<SkillPrompt, Long> {

    List<SkillPrompt> findAllByOrderByCreatedAtDesc();

    Page<SkillPrompt> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<SkillPrompt> findAllByOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT s FROM SkillPrompt s ORDER BY s.createdAt DESC")
    List<SkillPrompt> findRecentSkills(Pageable pageable);

    long count();
}