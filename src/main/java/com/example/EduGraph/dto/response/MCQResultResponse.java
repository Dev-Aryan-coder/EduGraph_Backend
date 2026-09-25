package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCQResultResponse {
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double score; // out of 20
    private Double percentage;
    private Boolean passed;
    private List<MCQBreakdownItem> breakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MCQBreakdownItem {
        private Long questionId;
        private Integer questionNumber;
        private String questionText;
        private String selectedOption;
        private String correctOption;
        private Boolean isCorrect;
        private String explanation;
    }
}
