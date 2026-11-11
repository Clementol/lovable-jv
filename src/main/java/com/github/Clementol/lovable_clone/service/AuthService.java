package com.github.Clementol.lovable_clone.service;

import com.github.Clementol.lovable_clone.dto.auth.AuthResponse;
import com.github.Clementol.lovable_clone.dto.auth.LoginRequest;
import com.github.Clementol.lovable_clone.dto.auth.SignupRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
