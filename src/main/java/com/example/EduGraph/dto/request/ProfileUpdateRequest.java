package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phoneNumber;

    private String profileImageUrl;
}
