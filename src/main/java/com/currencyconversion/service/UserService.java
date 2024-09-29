package com.currencyconversion.service;

import com.currencyconversion.request.UserRequest;
import com.currencyconversion.response.UserResponse;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);
}
