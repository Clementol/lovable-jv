package com.github.Clementol.lovable_clone.dto.member;

import com.github.Clementol.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull ProjectRole role) {
}
