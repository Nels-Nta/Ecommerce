package com.ntabana.backend.services;

import com.ntabana.backend.dto.UserDto;
import com.ntabana.backend.user.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();
}