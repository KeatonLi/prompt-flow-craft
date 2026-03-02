package com.promptflow.repository;

import com.promptflow.entity.SharedPrompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedPromptRepository extends JpaRepository<SharedPrompt, Long> {
    
    Page<SharedPrompt> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    Page<SharedPrompt> findByIsPublicTrueOrderByLikesDesc(Pageable pageable);
    
    Page<SharedPrompt> findByIsPublicTrueOrderByViewsDesc(Pageable pageable);
    
    List<SharedPrompt> findByAuthorNameOrderByCreatedAtDesc(String authorName);
    
    Page<SharedPrompt> findByCategoryIdOrderByCreatedAtDesc(Long categoryId, Pageable pageable);
    
    Page<SharedPrompt> findByIsPublicTrueAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
        String title, String content, Pageable pageable);
}
