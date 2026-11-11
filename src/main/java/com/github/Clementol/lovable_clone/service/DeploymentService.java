package com.github.Clementol.lovable_clone.service;

import com.github.Clementol.lovable_clone.dto.deploy.DeployResponse;

public interface DeploymentService {

    DeployResponse deploy(Long projectId);
}
