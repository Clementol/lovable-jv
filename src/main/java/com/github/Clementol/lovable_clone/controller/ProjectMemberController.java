package com.github.Clementol.lovable_clone.controller;

import com.github.Clementol.lovable_clone.dto.member.InviteMemberRequest;
import com.github.Clementol.lovable_clone.dto.member.MemberResponse;
import com.github.Clementol.lovable_clone.entity.ProjectMember;
import com.github.Clementol.lovable_clone.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {
    ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectMemberService.getProjectMember(projectId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable Long projectId,
            @RequestBody @Valid InviteMemberRequest request
    ) {
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(projectMemberService.inviteMember(projectId, request));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestBody @Valid InviteMemberRequest request
    ) {
        return  ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId, request));

    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteProjectMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId
    ) {
        projectMemberService.removeProjectMember(projectId, memberId);
        return ResponseEntity.noContent().build();
    }
}

