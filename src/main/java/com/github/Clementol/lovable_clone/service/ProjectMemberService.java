package com.github.Clementol.lovable_clone.service;

import com.github.Clementol.lovable_clone.dto.member.InviteMemberRequest;
import com.github.Clementol.lovable_clone.dto.member.MemberResponse;
import com.github.Clementol.lovable_clone.entity.ProjectMember;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectMemberService {
    List<MemberResponse> getProjectMember(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, InviteMemberRequest request);

    void removeProjectMember(Long projectId, Long memberId);
}
