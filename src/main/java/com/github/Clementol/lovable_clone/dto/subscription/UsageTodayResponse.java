package com.github.Clementol.lovable_clone.dto.subscription;

public record UsageTodayResponse(
        int tokenUsed,
        int tokensLimit,
        int previewRunning,
        int previewLimit
) {
}
