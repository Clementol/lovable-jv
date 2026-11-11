package com.github.Clementol.lovable_clone.dto.member;

import com.github.Clementol.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
        @NotBlank String username,
        @NotNull ProjectRole role
) {

}
