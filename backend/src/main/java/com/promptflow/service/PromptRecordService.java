package com.promptflow.service;

import com.promptflow.entity.AgentPrompt;
import com.promptflow.entity.SkillPrompt;
import com.promptflow.repository.AgentPromptRepository;
import com.promptflow.repository.SkillPromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PromptRecordService {

    @Autowired
    private AgentPromptRepository agentPromptRepository;

    @Autowired
    private SkillPromptRepository skillPromptRepository;

    // ==================== Agent Prompts ====================

    public List<AgentPrompt> getAllAgents() {
        return agentPromptRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AgentPrompt> getRecentAgents(int limit) {
        return agentPromptRepository.findRecentAgents(PageRequest.of(0, limit));
    }

    public Optional<AgentPrompt> getAgentById(Long id) {
        return agentPromptRepository.findById(id);
    }

    @Transactional
    public AgentPrompt saveAgent(AgentPrompt agent) {
        return agentPromptRepository.save(agent);
    }

    public long getAgentCount() {
        return agentPromptRepository.count();
    }

    public Page<AgentPrompt> getAgentPage(Pageable pageable) {
        return agentPromptRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<AgentPrompt> getAgentPageByLikeCount(Pageable pageable) {
        return agentPromptRepository.findAllByOrderByLikeCountDesc(pageable);
    }

    @Transactional
    public boolean deleteAgent(Long id) {
        if (agentPromptRepository.existsById(id)) {
            agentPromptRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean likeAgent(Long id) {
        return agentPromptRepository.findById(id)
                .map(agent -> {
                    Integer currentCount = agent.getLikeCount();
                    agent.setLikeCount(currentCount == null ? 1 : currentCount + 1);
                    agentPromptRepository.save(agent);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean unlikeAgent(Long id) {
        return agentPromptRepository.findById(id)
                .map(agent -> {
                    Integer currentCount = agent.getLikeCount();
                    if (currentCount != null && currentCount > 0) {
                        agent.setLikeCount(currentCount - 1);
                        agentPromptRepository.save(agent);
                    }
                    return true;
                })
                .orElse(false);
    }

    // ==================== Skill Prompts ====================

    public List<SkillPrompt> getAllSkills() {
        return skillPromptRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<SkillPrompt> getRecentSkills(int limit) {
        return skillPromptRepository.findRecentSkills(PageRequest.of(0, limit));
    }

    public Optional<SkillPrompt> getSkillById(Long id) {
        return skillPromptRepository.findById(id);
    }

    @Transactional
    public SkillPrompt saveSkill(SkillPrompt skill) {
        return skillPromptRepository.save(skill);
    }

    public long getSkillCount() {
        return skillPromptRepository.count();
    }

    public Page<SkillPrompt> getSkillPage(Pageable pageable) {
        return skillPromptRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<SkillPrompt> getSkillPageByLikeCount(Pageable pageable) {
        return skillPromptRepository.findAllByOrderByLikeCountDesc(pageable);
    }

    @Transactional
    public boolean deleteSkill(Long id) {
        if (skillPromptRepository.existsById(id)) {
            skillPromptRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean likeSkill(Long id) {
        return skillPromptRepository.findById(id)
                .map(skill -> {
                    Integer currentCount = skill.getLikeCount();
                    skill.setLikeCount(currentCount == null ? 1 : currentCount + 1);
                    skillPromptRepository.save(skill);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean unlikeSkill(Long id) {
        return skillPromptRepository.findById(id)
                .map(skill -> {
                    Integer currentCount = skill.getLikeCount();
                    if (currentCount != null && currentCount > 0) {
                        skill.setLikeCount(currentCount - 1);
                        skillPromptRepository.save(skill);
                    }
                    return true;
                })
                .orElse(false);
    }
}