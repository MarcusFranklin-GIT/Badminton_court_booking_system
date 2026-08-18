package com.courtbooking.batmition.dto;

import com.courtbooking.batmition.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private Set<User.Role> roles;
}
