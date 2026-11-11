package com.github.Clementol.lovable_clone.mapper;

import com.github.Clementol.lovable_clone.dto.project.ProjectResponse;
import com.github.Clementol.lovable_clone.dto.project.ProjectSummaryResponse;
import com.github.Clementol.lovable_clone.entity.Project;
import com.github.Clementol.lovable_clone.enums.ProjectRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
//    @Mapping(target = "owner", ignore = true)
    ProjectResponse toProjectResponse(Project project);

    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> projects);
}
