package com.promptflow.repository;

import com.promptflow.entity.PromptTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromptTagRepository extends JpaRepository<PromptTag, Long> {

    Optional<PromptTag> findByName(String name);

    List<PromptTag> findByIsSystemTrueOrderByUsageCountDesc();
}
