package com.example.EduGraph.dto.response;

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
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String status;
    private Long collegeId;
    private String collegeName;
    private Long classroomId;
    private String classroomName;
    private String rollNumber;
    private String phoneNumber;
    private String profileImageUrl;
}
