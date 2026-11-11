package com.github.Clementol.lovable_clone.mapper;

import com.github.Clementol.lovable_clone.dto.member.MemberResponse;
import com.github.Clementol.lovable_clone.entity.ProjectMember;
import com.github.Clementol.lovable_clone.entity.User;
import com.github.Clementol.lovable_clone.enums.ProjectRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "role", constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.name", target = "name")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);
}
