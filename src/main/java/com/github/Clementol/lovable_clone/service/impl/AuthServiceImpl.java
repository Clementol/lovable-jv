package com.github.Clementol.lovable_clone.service.impl;

import com.github.Clementol.lovable_clone.dto.auth.AuthResponse;
import com.github.Clementol.lovable_clone.dto.auth.LoginRequest;
import com.github.Clementol.lovable_clone.dto.auth.SignupRequest;
import com.github.Clementol.lovable_clone.entity.User;
import com.github.Clementol.lovable_clone.error.BadRequestException;
import com.github.Clementol.lovable_clone.mapper.UserMapper;
import com.github.Clementol.lovable_clone.repository.UserRepository;
import com.github.Clementol.lovable_clone.security.AuthUtil;
import com.github.Clementol.lovable_clone.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findUserByUsername(request.username()).ifPresent( user -> {
            throw new BadRequestException("User already exists with username: "+ request.username());
        });
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        String token = authUtil.generateAccessToken(user);
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            throw new BadRequestException("Authenticated principal is not a User");
        }
        String token = authUtil.generateAccessToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }
}
