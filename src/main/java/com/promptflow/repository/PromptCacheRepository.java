package com.promptflow.repository;

import com.promptflow.entity.PromptCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

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
     * 获取最近的历史记录
     * @return 最近的历史记录
     */
    @Query("SELECT p FROM PromptCache p ORDER BY p.createdAt DESC")
    List<PromptCache> findRecentHistory();
    
    /**
     * 获取历史记录（排除缓存，即hit_count=0的记录）
     * @return 历史记录列表
     */
    @Query("SELECT p FROM PromptCache p WHERE p.hitCount = 0 ORDER BY p.createdAt DESC")
    List<PromptCache> findHistoryRecords();
}