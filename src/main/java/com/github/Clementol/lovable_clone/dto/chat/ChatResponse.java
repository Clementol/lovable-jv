package com.github.Clementol.lovable_clone.dto.chat;

import com.github.Clementol.lovable_clone.entity.ChatEvent;
import com.github.Clementol.lovable_clone.entity.ChatSession;
import com.github.Clementol.lovable_clone.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
//        ChatSession chatSession,
        MessageRole role,
        List<ChatEventResponse> events,
        String content,
        Integer tokenUsed,
        Instant createdAt
) {
}
