package com.promptflow.repository;

import com.promptflow.entity.PromptCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PromptCacheRepository extends JpaRepository<PromptCache, Long> {
    
    /**
     * 根据请求哈希查找缓存
     * @param requestHash 请求参数的哈希值
     * @return 缓存记录
     */
    Optional<PromptCache> findByRequestHash(String requestHash);
    
    /**
     * 检查是否存在指定哈希的缓存
     * @param requestHash 请求参数的哈希值
     * @return 是否存在
     */
    boolean existsByRequestHash(String requestHash);
    
    /**
     * 获取缓存命中次数统计
     * @return 总命中次数
     */
    @Query("SELECT SUM(p.hitCount) FROM PromptCache p")
    Long getTotalHitCount();
    
    /**
     * 获取缓存总数
     * @return 缓存记录总数
     */
    @Query("SELECT COUNT(p) FROM PromptCache p")
    Long getTotalCacheCount();
    
    /**
     * 根据任务描述模糊查询
     * @param taskDescription 任务描述关键词
     * @return 匹配的缓存记录
     */
    @Query("SELECT p FROM PromptCache p WHERE p.taskDescription LIKE %:taskDescription%")
    List<PromptCache> findByTaskDescriptionContaining(@Param("taskDescription") String taskDescription);
    
    /**
     * 获取所有历史记录，按创建时间降序排序
     * @return 历史记录列表
     */
    List<PromptCache> findAllByOrderByCreatedAtDesc();
    
    /**
     * 查询最近的历史记录（只查摘要，用于列表展示）
     * @param pageable 分页参数
     * @return 最近的历史记录（包含摘要信息）
     */
    @Query("SELECT p.id, p.taskDescription, p.targetAudience, p.promptSummary, p.createdAt, p.hitCount, p.categoryId, p.likeCount, p.isAutoTagged, p.usageScenario, p.effectivenessScore, p.aiTags FROM PromptCache p ORDER BY p.createdAt DESC")
    List<Object[]> findRecentHistorySummary(Pageable pageable);
    
    /**
     * 获取历史记录（排除缓存，即hit_count=0的记录）
     * @return 历史记录列表
     */
    @Query("SELECT p FROM PromptCache p WHERE p.hitCount = 0 ORDER BY p.createdAt DESC")
    List<PromptCache> findHistoryRecords();

    // ==================== 新增分类和标签相关查询 ====================

    /**
     * 根据分类ID查询提示词（分页）
     */
    Page<PromptCache> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 根据分类ID查询提示词
     */
    List<PromptCache> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    /**
     * 根据点赞数查询提示词（分页）
     */
    @Query("SELECT p FROM PromptCache p ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<PromptCache> findAllOrderByLikeCountDesc(Pageable pageable);

    /**
     * 查询点赞数大于0的提示词（分页）
     */
    @Query("SELECT p FROM PromptCache p WHERE p.likeCount > 0 ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<PromptCache> findByLikeCountGreaterThanZeroOrderByLikeCountDesc(Pageable pageable);

    /**
     * 根据标签ID查询提示词（分页）
     */
    @Query("SELECT p FROM PromptCache p JOIN p.tags t WHERE t.id = :tagId ORDER BY p.createdAt DESC")
    Page<PromptCache> findByTagId(@Param("tagId") Long tagId, Pageable pageable);

    /**
     * 根据多个标签ID查询提示词（包含任一标签即可）
     */
    @Query("SELECT DISTINCT p FROM PromptCache p JOIN p.tags t WHERE t.id IN :tagIds ORDER BY p.createdAt DESC")
    Page<PromptCache> findByTagIds(@Param("tagIds") Set<Long> tagIds, Pageable pageable);

    /**
     * 综合筛选查询（分类 + 关键词 + 排序方式）
     */
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

    /**
     * 统计各分类的提示词数量
     */
    @Query("SELECT p.categoryId, COUNT(p) FROM PromptCache p WHERE p.categoryId IS NOT NULL GROUP BY p.categoryId")
    List<Object[]> countByCategory();

    /**
     * 统计有点赞的记录数量
     */
//     long countByLikeCountGreaterThan(int likeCount);

    /**
     * 查询需要自动打标签的提示词（未打过标签的）
     */
    @Query("SELECT p FROM PromptCache p WHERE p.isAutoTagged = false OR p.isAutoTagged IS NULL ORDER BY p.createdAt DESC")
    List<PromptCache> findUnTaggedPrompts();

    /**
     * 查询最近创建的提示词（用于批量打标签）
     */
    @Query("SELECT p FROM PromptCache p ORDER BY p.createdAt DESC")
    List<PromptCache> findRecentPrompts(Pageable pageable);

    /**
     * 查询评分最高的提示词（分页）
     */
    @Query("SELECT p FROM PromptCache p WHERE p.averageRating IS NOT NULL ORDER BY p.averageRating DESC, p.createdAt DESC")
    Page<PromptCache> findByAverageRatingIsNotNullOrderByAverageRatingDesc(Pageable pageable);
    
    // ==================== 统计相关查询 ====================

    /**
     * 统计今日创建的提示词数量
     */
    @Query(value = "SELECT COUNT(*) FROM prompt_cache WHERE DATE(created_at) = CURDATE()", nativeQuery = true)
    long countToday();

    /**
     * 统计本周创建的提示词数量
     */
    @Query(value = "SELECT COUNT(*) FROM prompt_cache WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)", nativeQuery = true)
    long countThisWeek();

    /**
     * 统计本月创建的提示词数量
     */
    @Query(value = "SELECT COUNT(*) FROM prompt_cache WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)", nativeQuery = true)
    long countThisMonth();
    
    /**
     * 统计总点赞数
     */
    @Query("SELECT COALESCE(SUM(p.likeCount), 0) FROM PromptCache p")
    Long sumTotalLikes();
    
    /**
     * 统计平均评分
     */
    @Query("SELECT AVG(p.averageRating) FROM PromptCache p WHERE p.averageRating IS NOT NULL")
    Double getAverageRating();
    /**
     * 统计每日创建数和点赞数（最近N天）
     * 使用标准SQL以保证跨数据库兼容
     */
    @Query(value = "SELECT CAST(created_at AS DATE) as day, COUNT(*) as count, COALESCE(SUM(like_count), 0) as likes " +
           "FROM prompt_cache WHERE created_at >= :startDate GROUP BY CAST(created_at AS DATE) ORDER BY day", 
           nativeQuery = true)
    List<Object[]> countByDayWithLikes(@Param("startDate") java.time.LocalDateTime startDate);
    
    /**
     * 统计每日创建数和点赞数（使用JPQL，兼容性更好）
     */
    @Query("SELECT FUNCTION('DATE', p.createdAt), COUNT(p), COALESCE(SUM(p.likeCount), 0) FROM PromptCache p WHERE p.createdAt >= :startDate GROUP BY FUNCTION('DATE', p.createdAt) ORDER BY FUNCTION('DATE', p.createdAt)")
    List<Object[]> countByDayWithLikesJpql(@Param("startDate") java.time.LocalDateTime startDate);
    
    /**
     * 获取总浏览次数
     */
    @Query("SELECT COALESCE(SUM(p.hitCount), 0) FROM PromptCache p")
    Long sumTotalHits();
}