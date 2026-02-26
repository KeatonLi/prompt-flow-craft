package com.promptflow.repository;

import com.promptflow.entity.PromptCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromptCategoryRepository extends JpaRepository<PromptCategory, Long> {

    // 根据名称查找分类
    Optional<PromptCategory> findByName(String name);

    // 查找所有系统预设分类，按排序顺序
    List<PromptCategory> findByIsSystemTrueOrderBySortOrderAsc();

    // 查找所有分类，按排序顺序
    List<PromptCategory> findAllByOrderBySortOrderAsc();

    // 查找用户自定义分类
    List<PromptCategory> findByIsSystemFalseOrderBySortOrderAsc();

    // 检查分类名称是否存在
    boolean existsByName(String name);
}
