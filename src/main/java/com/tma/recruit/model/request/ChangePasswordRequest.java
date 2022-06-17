package com.tma.recruit.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @Size(min = 8, max = 50, message = "Password length must be at least 8 characters")
    private String oldPassword;

    @Size(min = 8, max = 50, message = "Password length must be at least 8 characters")
    private String newPassword;
}
