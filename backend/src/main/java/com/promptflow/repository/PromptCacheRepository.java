package com.promptflow.repository;

import com.promptflow.entity.PromptCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PromptCacheRepository extends JpaRepository<PromptCache, Long> {

    Optional<PromptCache> findByRequestHash(String requestHash);

    boolean existsByRequestHash(String requestHash);

    @Query("SELECT SUM(p.hitCount) FROM PromptCache p")
    Long getTotalHitCount();

    @Query("SELECT COUNT(p) FROM PromptCache p")
    Long getTotalCacheCount();

    @Query("SELECT p FROM PromptCache p WHERE p.taskDescription LIKE %:taskDescription%")
    List<PromptCache> findByTaskDescriptionContaining(@Param("taskDescription") String taskDescription);

    List<PromptCache> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p.id, p.taskDescription, p.targetAudience, p.promptSummary, p.createdAt, p.hitCount, p.categoryId, p.likeCount, p.isAutoTagged, p.usageScenario, p.effectivenessScore, p.aiTags FROM PromptCache p ORDER BY p.createdAt DESC")
    List<Object[]> findRecentHistorySummary(Pageable pageable);

    List<PromptCache> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    @Query("SELECT p FROM PromptCache p ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<PromptCache> findAllOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT p FROM PromptCache p WHERE p.likeCount > 0 ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<PromptCache> findByLikeCountGreaterThanZeroOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT DISTINCT p FROM PromptCache p JOIN p.tags t WHERE t.id IN :tagIds ORDER BY p.createdAt DESC")
    Page<PromptCache> findByTagIds(@Param("tagIds") Set<Long> tagIds, Pageable pageable);

    @Query(value = "SELECT * FROM prompt_cache p WHERE " +
           "(:categoryId IS NULL OR p.category_id = :categoryId) AND " +
           "(:keyword IS NULL OR p.task_description LIKE CONCAT('%', :keyword, '%') OR p.generated_prompt LIKE CONCAT('%', :keyword, '%')) " +
           "ORDER BY CASE WHEN :sortBy = 'likeCount' THEN p.like_count ELSE 0 END DESC, p.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM prompt_cache p WHERE " +
           "(:categoryId IS NULL OR p.category_id = :categoryId) AND " +
           "(:keyword IS NULL OR p.task_description LIKE CONCAT('%', :keyword, '%') OR p.generated_prompt LIKE CONCAT('%', :keyword, '%'))",
           nativeQuery = true)
    Page<PromptCache> findByFilters(@Param("categoryId") Long categoryId,
                                    @Param("keyword") String keyword,
                                    @Param("sortBy") String sortBy,
                                    Pageable pageable);

    @Query("SELECT p.categoryId, COUNT(p) FROM PromptCache p WHERE p.categoryId IS NOT NULL GROUP BY p.categoryId")
    List<Object[]> countByCategory();

    @Query("SELECT p FROM PromptCache p WHERE p.isAutoTagged = false OR p.isAutoTagged IS NULL ORDER BY p.createdAt DESC")
    List<PromptCache> findUnTaggedPrompts();

    @Query("SELECT p FROM PromptCache p WHERE p.averageRating IS NOT NULL ORDER BY p.averageRating DESC, p.createdAt DESC")
    Page<PromptCache> findByAverageRatingIsNotNullOrderByAverageRatingDesc(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM prompt_cache WHERE DATE(created_at) = CURDATE()", nativeQuery = true)
    long countToday();

    @Query(value = "SELECT COUNT(*) FROM prompt_cache WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)", nativeQuery = true)
    long countThisWeek();

    @Query(value = "SELECT COUNT(*) FROM prompt_cache WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)", nativeQuery = true)
    long countThisMonth();

    @Query("SELECT COALESCE(SUM(p.likeCount), 0) FROM PromptCache p")
    Long sumTotalLikes();

    @Query("SELECT AVG(p.averageRating) FROM PromptCache p WHERE p.averageRating IS NOT NULL")
    Double getAverageRating();

    @Query(value = "SELECT CAST(created_at AS DATE) as day, COUNT(*) as count, COALESCE(SUM(like_count), 0) as likes " +
           "FROM prompt_cache WHERE created_at >= :startDate GROUP BY CAST(created_at AS DATE) ORDER BY day",
           nativeQuery = true)
    List<Object[]> countByDayWithLikes(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(p.hitCount), 0) FROM PromptCache p")
    Long sumTotalHits();

    @Query("SELECT COALESCE(SUM(p.inputTokens), 0) FROM PromptCache p")
    Long sumTotalInputTokens();

    @Query("SELECT COALESCE(SUM(p.outputTokens), 0) FROM PromptCache p")
    Long sumTotalOutputTokens();

    @Query("SELECT FUNCTION('DATE', p.createdAt), COUNT(p), COALESCE(SUM(p.likeCount), 0) FROM PromptCache p WHERE p.createdAt >= :startDate GROUP BY FUNCTION('DATE', p.createdAt) ORDER BY FUNCTION('DATE', p.createdAt)")
    List<Object[]> countByDayWithLikesJpql(@Param("startDate") LocalDateTime startDate);
}
