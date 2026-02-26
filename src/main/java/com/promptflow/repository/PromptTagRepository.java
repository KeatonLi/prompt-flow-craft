package com.promptflow.repository;

import com.promptflow.entity.PromptTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PromptTagRepository extends JpaRepository<PromptTag, Long> {

    // 根据名称查找标签
    Optional<PromptTag> findByName(String name);

    // 根据名称集合查找标签
    Set<PromptTag> findByNameIn(Set<String> names);

    // 查找所有系统预设标签
    List<PromptTag> findByIsSystemTrueOrderByUsageCountDesc();

    // 查找热门标签（按使用次数排序）
    List<PromptTag> findTop10ByOrderByUsageCountDesc();

    // 查找所有标签，按使用次数排序
    List<PromptTag> findAllByOrderByUsageCountDesc();

    // 检查标签名称是否存在
    boolean existsByName(String name);

    // 根据提示词ID查找关联的标签
    @Query("SELECT t FROM PromptTag t JOIN t.prompts p WHERE p.id = :promptId")
    Set<PromptTag> findByPromptId(Long promptId);

    // 搜索标签（模糊匹配）
    List<PromptTag> findByNameContainingIgnoreCaseOrderByUsageCountDesc(String keyword);
}
