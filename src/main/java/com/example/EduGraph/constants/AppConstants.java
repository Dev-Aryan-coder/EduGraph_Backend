package com.example.EduGraph.constants;

public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    public static final int MCQ_QUESTION_COUNT = 20;
    public static final int MAX_DEADLINE_EXTENSION_DAYS = 2;
    public static final int GRADING_DEFAULT_MAX_MARKS = 10;
    public static final int MAX_FILE_SIZE_MB = 10;
    public static final long JWT_EXPIRATION_DEFAULT_MS = 86400000L; // 24 hours
}
