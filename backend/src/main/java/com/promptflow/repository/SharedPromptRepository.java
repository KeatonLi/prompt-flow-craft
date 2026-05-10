package com.promptflow.repository;

import com.promptflow.entity.SharedPrompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedPromptRepository extends JpaRepository<SharedPrompt, Long> {

    Page<SharedPrompt> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<SharedPrompt> findAllByOrderByLikeCountDescCreatedAtDesc(Pageable pageable);

    List<SharedPrompt> findTop20ByOrderByCreatedAtDesc();

    @Query("SELECT s FROM SharedPrompt s ORDER BY s.likeCount DESC, s.createdAt DESC")
    Page<SharedPrompt> findTopLiked(Pageable pageable);

    @Modifying
    @Query("UPDATE SharedPrompt s SET s.likeCount = s.likeCount + 1 WHERE s.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE SharedPrompt s SET s.likeCount = s.likeCount - 1 WHERE s.id = :id AND s.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE SharedPrompt s SET s.viewCount = s.viewCount + 1 WHERE s.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
