package com.promptflow.service;

import com.promptflow.dto.PublishResponse;
import com.promptflow.dto.ShareRequest;
import com.promptflow.dto.SharedPromptResponse;
import com.promptflow.entity.SharedPrompt;
import com.promptflow.repository.SharedPromptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SharedPromptService {

    private static final long LIKE_COOLDOWN_SECONDS = 60;

    private final SharedPromptRepository repository;

    public SharedPromptService(SharedPromptRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PublishResponse publish(ShareRequest request) {
        SharedPrompt sharedPrompt = new SharedPrompt();
        sharedPrompt.setDescription(request.getDescription());
        sharedPrompt.setPromptContent(request.getPromptContent());
        sharedPrompt.setAuthorNickname(request.getAuthorNickname());
        sharedPrompt.setAuthorContact(request.getAuthorContact());
        sharedPrompt.setSourcePromptId(request.getSourcePromptId());
        sharedPrompt.setDeleteToken(UUID.randomUUID().toString().replace("-", ""));
        sharedPrompt.setLikeCount(0);
        sharedPrompt.setViewCount(0);

        SharedPrompt saved = repository.save(sharedPrompt);
        return new PublishResponse(saved.getId(), saved.getDeleteToken());
    }

    @Transactional
    public Optional<SharedPromptResponse> getById(Long id) {
        Optional<SharedPrompt> optional = repository.findById(id);
        if (optional.isPresent()) {
            repository.incrementViewCount(id);
            return optional.map(this::toResponse);
        }
        return Optional.empty();
    }

    public Page<SharedPromptResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return repository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    public Page<SharedPromptResponse> getTopLiked(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return repository.findTopLiked(pageable)
                .map(this::toResponse);
    }

    public List<SharedPromptResponse> getRecent(int limit) {
        return repository.findTop20ByOrderByCreatedAtDesc().stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean delete(Long id, String token) {
        Optional<SharedPrompt> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return false;
        }
        SharedPrompt prompt = optional.get();
        if (!prompt.getDeleteToken().equals(token)) {
            return false;
        }
        repository.delete(prompt);
        return true;
    }

    @Transactional
    public boolean like(Long id) {
        Optional<SharedPrompt> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return false;
        }
        SharedPrompt prompt = optional.get();

        if (prompt.getLastLikeTime() != null) {
            long secondsSinceLastLike = java.time.Duration.between(
                    prompt.getLastLikeTime(), LocalDateTime.now()).getSeconds();
            if (secondsSinceLastLike < LIKE_COOLDOWN_SECONDS) {
                return false;
            }
        }

        repository.incrementLikeCount(id);
        prompt.setLastLikeTime(LocalDateTime.now());
        repository.save(prompt);
        return true;
    }

    @Transactional
    public boolean unlike(Long id) {
        Optional<SharedPrompt> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return false;
        }
        repository.decrementLikeCount(id);
        return true;
    }

    private SharedPromptResponse toResponse(SharedPrompt prompt) {
        SharedPromptResponse response = new SharedPromptResponse();
        response.setId(prompt.getId());
        response.setDescription(prompt.getDescription());
        response.setPromptContent(prompt.getPromptContent());
        response.setAuthorNickname(prompt.getAuthorNickname());
        response.setAuthorContact(prompt.getAuthorContact());
        response.setLikeCount(prompt.getLikeCount());
        response.setViewCount(prompt.getViewCount());
        response.setSourcePromptId(prompt.getSourcePromptId());
        response.setCreatedAt(prompt.getCreatedAt());
        response.setUpdatedAt(prompt.getUpdatedAt());
        return response;
    }
}