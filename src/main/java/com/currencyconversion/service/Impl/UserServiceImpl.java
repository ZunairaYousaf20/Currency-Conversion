package com.currencyconversion.service.Impl;

import com.currencyconversion.request.UserRequest;
import com.currencyconversion.response.UserResponse;
import com.currencyconversion.model.User;
import com.currencyconversion.repository.UserRepository;
import com.currencyconversion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User savedUser = userRepository.save(buildUserObject(userRequest));
        return UserResponse.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .userType(savedUser.getUserType())
                .phoneNo(savedUser.getPhoneNo())
                .joiningDate(savedUser.getJoiningDate())
                .build();
    }

    private User buildUserObject(UserRequest userRequest) {
        return User.builder().name(userRequest.getName())
                .role(userRequest.getRole())
                .userType(userRequest.getUserType())
                .email(userRequest.getEmail())
                .phoneNo(userRequest.getPhoneNo())
                .joiningDate(userRequest.getJoiningDate())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();

    }
}
