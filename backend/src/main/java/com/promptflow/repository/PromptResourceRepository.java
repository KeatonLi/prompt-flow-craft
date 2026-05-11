package com.promptflow.repository;

import com.promptflow.entity.PromptResource;
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
public interface PromptResourceRepository extends JpaRepository<PromptResource, Long> {

    Optional<PromptResource> findByRequestHash(String requestHash);

    boolean existsByRequestHash(String requestHash);

    @Query("SELECT SUM(p.hitCount) FROM PromptResource p")
    Long getTotalHitCount();

    @Query("SELECT COUNT(p) FROM PromptResource p")
    Long getTotalCacheCount();

    @Query("SELECT p FROM PromptResource p WHERE p.name LIKE %:name% OR p.generatedPrompt LIKE %:name%")
    List<PromptResource> findByNameContaining(@Param("name") String name);

    List<PromptResource> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p.id, p.name, p.promptSummary, p.createdAt, p.hitCount, p.categoryId, p.likeCount, p.isAutoTagged, p.usageScenario, p.effectivenessScore, p.aiTags, p.promptType FROM PromptResource p ORDER BY p.createdAt DESC")
    List<Object[]> findRecentHistorySummary(Pageable pageable);

    List<PromptResource> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    @Query("SELECT p FROM PromptResource p ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<PromptResource> findAllOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT p FROM PromptResource p WHERE p.likeCount > 0 ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<PromptResource> findByLikeCountGreaterThanZeroOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT DISTINCT p FROM PromptResource p JOIN p.tags t WHERE t.id IN :tagIds ORDER BY p.createdAt DESC")
    Page<PromptResource> findByTagIds(@Param("tagIds") Set<Long> tagIds, Pageable pageable);

    @Query(value = "SELECT * FROM prompt_resource p WHERE " +
           "(:categoryId IS NULL OR p.category_id = :categoryId) AND " +
           "(:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%') OR p.generated_prompt LIKE CONCAT('%', :keyword, '%')) " +
           "ORDER BY CASE WHEN :sortBy = 'likeCount' THEN p.like_count ELSE 0 END DESC, p.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM prompt_resource p WHERE " +
           "(:categoryId IS NULL OR p.category_id = :categoryId) AND " +
           "(:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%') OR p.generated_prompt LIKE CONCAT('%', :keyword, '%'))",
           nativeQuery = true)
    Page<PromptResource> findByFilters(@Param("categoryId") Long categoryId,
                                    @Param("keyword") String keyword,
                                    @Param("sortBy") String sortBy,
                                    Pageable pageable);

    @Query("SELECT p.categoryId, COUNT(p) FROM PromptResource p WHERE p.categoryId IS NOT NULL GROUP BY p.categoryId")
    List<Object[]> countByCategory();

    @Query("SELECT p FROM PromptResource p WHERE p.isAutoTagged = false OR p.isAutoTagged IS NULL ORDER BY p.createdAt DESC")
    List<PromptResource> findUnTaggedPrompts();

    @Query("SELECT p FROM PromptResource p WHERE p.averageRating IS NOT NULL ORDER BY p.averageRating DESC, p.createdAt DESC")
    Page<PromptResource> findByAverageRatingIsNotNullOrderByAverageRatingDesc(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM prompt_resource WHERE DATE(created_at) = CURDATE()", nativeQuery = true)
    long countToday();

    @Query(value = "SELECT COUNT(*) FROM prompt_resource WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)", nativeQuery = true)
    long countThisWeek();

    @Query(value = "SELECT COUNT(*) FROM prompt_resource WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)", nativeQuery = true)
    long countThisMonth();

    @Query("SELECT COALESCE(SUM(p.likeCount), 0) FROM PromptResource p")
    Long sumTotalLikes();

    @Query("SELECT AVG(p.averageRating) FROM PromptResource p WHERE p.averageRating IS NOT NULL")
    Double getAverageRating();

    @Query(value = "SELECT CAST(created_at AS DATE) as day, COUNT(*) as count, COALESCE(SUM(like_count), 0) as likes " +
           "FROM prompt_resource WHERE created_at >= :startDate GROUP BY CAST(created_at AS DATE) ORDER BY day",
           nativeQuery = true)
    List<Object[]> countByDayWithLikes(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(p.hitCount), 0) FROM PromptResource p")
    Long sumTotalHits();

    @Query("SELECT COALESCE(SUM(p.inputTokens), 0) FROM PromptResource p")
    Long sumTotalInputTokens();

    @Query("SELECT COALESCE(SUM(p.outputTokens), 0) FROM PromptResource p")
    Long sumTotalOutputTokens();

    @Query("SELECT FUNCTION('DATE', p.createdAt), COUNT(p), COALESCE(SUM(p.likeCount), 0) FROM PromptResource p WHERE p.createdAt >= :startDate GROUP BY FUNCTION('DATE', p.createdAt) ORDER BY FUNCTION('DATE', p.createdAt)")
    List<Object[]> countByDayWithLikesJpql(@Param("startDate") LocalDateTime startDate);
}
