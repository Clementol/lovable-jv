package com.github.Clementol.lovable_clone.dto.member;

import com.github.Clementol.lovable_clone.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        String avatarUrl,
        ProjectRole role,
        Instant invitedAt
) {
}
