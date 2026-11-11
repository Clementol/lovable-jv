package com.github.Clementol.lovable_clone.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.github.Clementol.lovable_clone.enums.ProjectPermission.*;

@Getter
public enum ProjectRole {
    EDITOR(VIEW, EDIT, DELETE, VIEW_MEMBERS),
    VIEWER(VIEW, VIEW_MEMBERS),
    OWNER(VIEW, EDIT, DELETE, MANAGE_MEMBERS, VIEW_MEMBERS);

    private final Set<ProjectPermission> permissions;

    ProjectRole(ProjectPermission... permissions) {
        this.permissions = Set.of(permissions);
    }

}
