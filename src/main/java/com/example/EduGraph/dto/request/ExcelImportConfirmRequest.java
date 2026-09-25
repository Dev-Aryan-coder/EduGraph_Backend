package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelImportConfirmRequest {

    private Long classroomId;

    @NotEmpty(message = "Confirmed row list cannot be empty")
    private List<UserRowItem> users;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRowItem {
        private String fullName;
        private String email;
        private String rollNumber;
        private String phoneNumber;
        private String initialPassword;
    }
}
