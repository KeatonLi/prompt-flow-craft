package com.promptflow.repository;

import com.promptflow.entity.PromptCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromptCategoryRepository extends JpaRepository<PromptCategory, Long> {

    Optional<PromptCategory> findByName(String name);

    List<PromptCategory> findByIsSystemTrueOrderBySortOrderAsc();
}
