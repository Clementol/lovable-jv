package com.github.Clementol.lovable_clone.service;

import com.github.Clementol.lovable_clone.dto.chat.ChatResponse;
import com.github.Clementol.lovable_clone.entity.ChatMessage;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);
}














