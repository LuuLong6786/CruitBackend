package com.tma.recruit.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Email
    private String email;

    @Size(min = 8, max = 50, message = "Password length must be at least 8 characters")
    private String password;
}