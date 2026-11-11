package com.github.Clementol.lovable_clone.service.impl;

import com.github.Clementol.lovable_clone.dto.member.InviteMemberRequest;
import com.github.Clementol.lovable_clone.dto.member.MemberResponse;
import com.github.Clementol.lovable_clone.entity.Project;
import com.github.Clementol.lovable_clone.entity.ProjectMember;
import com.github.Clementol.lovable_clone.entity.ProjectMemberId;
import com.github.Clementol.lovable_clone.entity.User;
import com.github.Clementol.lovable_clone.mapper.ProjectMemberMapper;
import com.github.Clementol.lovable_clone.repository.ProjectMemberRepository;
import com.github.Clementol.lovable_clone.repository.ProjectRepository;
import com.github.Clementol.lovable_clone.repository.UserRepository;
import com.github.Clementol.lovable_clone.security.AuthUtil;
import com.github.Clementol.lovable_clone.service.ProjectMemberService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMemberMapper projectMemberMapper;
    AuthUtil authUtil;

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMember(Long projectId) {
        Long userId = authUtil.getCurrentUser();
        Project project = getAccessibleProjectById(projectId, userId);

       return projectMemberRepository.findById_ProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromMember)
                .toList();
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUser();
        Project project = getAccessibleProjectById(projectId, userId);

        User invitee = userRepository.findUserByUsername(request.username()).orElseThrow();
//        if (invitee.getId().equals(project.getOwner().getId())) {
//            throw new RuntimeException("Not Allowed to Invite Yourself");
//        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());
        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Project Member already exists");
        }

        ProjectMember member = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .role(request.role())
                .invitedAt(Instant.now())
                .build();
        projectMemberRepository.save(member);
        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUser();
        Project project = getAccessibleProjectById(projectId, userId);


        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();
        projectMember.setRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void removeProjectMember(Long projectId, Long memberId) {
        Long userId = authUtil.getCurrentUser();
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Project Member not found");
        }
    }

    private Project getAccessibleProjectById(Long id, Long userId) {
        return projectRepository.findAllAccessibleByProjectId(id, userId).orElseThrow();
    }
}
