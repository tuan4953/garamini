package com.garage.user.dto;

import com.garage.user.model.Role;
import com.garage.user.model.User;

public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private Role role;
    private boolean enabled;

    public static UserResponse fromEntity(User user) {

        UserResponse response = new UserResponse();

        response.id = user.getId();
        response.fullName = user.getFullName();
        response.email = user.getEmail();
        response.phone = user.getPhone();
        response.address = user.getAddress();
        response.avatar = user.getAvatar();
        response.role = user.getRole();
        response.enabled = user.isEnabled();

        return response;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatar() {
        return avatar;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }
}