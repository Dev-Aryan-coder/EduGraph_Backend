package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrincipalRegisterRequest {

    @NotBlank(message = "College name is required")
    private String collegeName;

    private String collegeAddress;

    @Email(message = "Invalid college contact email")
    private String contactEmail;

    @NotBlank(message = "Principal full name is required")
    private String fullName;

    @NotBlank(message = "Principal email is required")
    @Email(message = "Invalid principal email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
