package com.promptflow.repository;

import com.promptflow.entity.AgentPrompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentPromptRepository extends JpaRepository<AgentPrompt, Long> {

    List<AgentPrompt> findAllByOrderByCreatedAtDesc();

    Page<AgentPrompt> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AgentPrompt> findAllByOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT a FROM AgentPrompt a ORDER BY a.createdAt DESC")
    List<AgentPrompt> findRecentAgents(Pageable pageable);

    long count();
}