package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelPreviewResponse {

    private int totalRows;
    private int validRows;
    private int invalidRows;
    private List<RowItem> rows;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RowItem {
        private int rowIndex;
        private String fullName;
        private String email;
        private String rollNumber;
        private String phoneNumber;
        private String classroomName;
        private boolean valid;
        private Map<String, String> errors;
    }
}
