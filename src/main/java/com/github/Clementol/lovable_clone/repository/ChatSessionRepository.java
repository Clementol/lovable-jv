package com.github.Clementol.lovable_clone.repository;

import com.github.Clementol.lovable_clone.entity.ChatSession;
import com.github.Clementol.lovable_clone.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}