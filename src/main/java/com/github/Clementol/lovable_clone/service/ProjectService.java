package com.github.Clementol.lovable_clone.service;

import com.github.Clementol.lovable_clone.dto.project.ProjectRequest;
import com.github.Clementol.lovable_clone.dto.project.ProjectResponse;
import com.github.Clementol.lovable_clone.dto.project.ProjectSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService {

    List<ProjectSummaryResponse> getUserProjects();

    ProjectSummaryResponse getUserProjectById(Long id);

    ProjectResponse crateProject(ProjectRequest request);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);
}
