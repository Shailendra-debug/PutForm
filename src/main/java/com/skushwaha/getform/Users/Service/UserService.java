package com.skushwaha.getform.Users.Service;

import com.skushwaha.getform.Users.DTO.UpdateProfile;
import com.skushwaha.getform.Users.DTO.UserResponse;
import com.skushwaha.getform.Users.Entity.User;

public interface UserService {

    UserResponse getCurrentUser(String usernameOrEmail);
    UserResponse updateProfile(String usernameOrEmail, UpdateProfile request);
    User findEntityByUsernameOrEmail(String usernameOrEmail);
}
