package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareRequest {

    @NotBlank(message = "Recipient student email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;
}
