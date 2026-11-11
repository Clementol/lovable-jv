package com.github.Clementol.lovable_clone.service.impl;

import com.github.Clementol.lovable_clone.dto.project.ProjectRequest;
import com.github.Clementol.lovable_clone.dto.project.ProjectResponse;
import com.github.Clementol.lovable_clone.dto.project.ProjectSummaryResponse;
import com.github.Clementol.lovable_clone.entity.Project;
import com.github.Clementol.lovable_clone.entity.ProjectMember;
import com.github.Clementol.lovable_clone.entity.ProjectMemberId;
import com.github.Clementol.lovable_clone.entity.User;
import com.github.Clementol.lovable_clone.enums.ProjectRole;
import com.github.Clementol.lovable_clone.error.BadRequestException;
import com.github.Clementol.lovable_clone.error.ResourceNotFoundException;
import com.github.Clementol.lovable_clone.mapper.ProjectMapper;
import com.github.Clementol.lovable_clone.repository.ProjectMemberRepository;
import com.github.Clementol.lovable_clone.repository.ProjectRepository;
import com.github.Clementol.lovable_clone.repository.UserRepository;
import com.github.Clementol.lovable_clone.security.AuthUtil;
import com.github.Clementol.lovable_clone.service.ProjectService;
import com.github.Clementol.lovable_clone.service.ProjectTemplateService;
import com.github.Clementol.lovable_clone.service.SubscriptionService;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMemberRepository projectMemberRepository;
    ProjectMapper projectMapper;
    AuthUtil authUtil;
    SubscriptionService subscriptionService;
    ProjectTemplateService projectTemplateService;

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUser();
        var projectsWithRoles = projectRepository.findAllAccessibleByUser(userId);
        return projectsWithRoles.stream()
                .map(p -> projectMapper.toProjectSummaryResponse(p.getProject(), p.getRole()))
                .toList();

    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectSummaryResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUser();
//        Project project = getAccessibleProjectById(projectId, userId);
        var projectWithRole = projectRepository.findAllAccessibleByProjectIdWithRole(projectId, userId)
                .orElseThrow(() -> new BadRequestException("Project Not Found"));
        return projectMapper.toProjectSummaryResponse(projectWithRole.getProject(), projectWithRole.getRole());
    }

    @Override
    public ProjectResponse crateProject(ProjectRequest request) {
        Long userId = authUtil.getCurrentUser();
//        User owner = userRepository.findById(userId).orElseThrow(
//                () -> new ResourceNotFoundException("User", userId.toString())
//        );

        if (!subscriptionService.canCreateNewProject()) {
            throw new BadRequestException("Upgrade to create new project");
        }
        User owner = userRepository.getReferenceById(userId);
        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);
        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .role(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);
        projectTemplateService.initializeProjectFromTemplate(project.getId());
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUser();
        Project project = getAccessibleProjectById(projectId, userId);

        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUser();
        Project project = getAccessibleProjectById(projectId, userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    private Project getAccessibleProjectById(Long id, Long userId) {
        return projectRepository.findAllAccessibleByProjectId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id.toString()));
    }
}
