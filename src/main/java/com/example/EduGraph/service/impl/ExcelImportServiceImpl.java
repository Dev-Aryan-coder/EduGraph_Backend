package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.ExcelImportConfirmRequest;
import com.example.EduGraph.dto.response.ExcelPreviewResponse;
import com.example.EduGraph.entity.Classroom;
import com.example.EduGraph.entity.College;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.AccountStatus;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.repository.ClassroomRepository;
import com.example.EduGraph.repository.CollegeRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.EmailService;
import com.example.EduGraph.service.ExcelImportService;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service

@Slf4j
public class ExcelImportServiceImpl implements ExcelImportService {

    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final ClassroomRepository classroomRepository;
    private final EmailService emailService;

    public ExcelImportServiceImpl(UserRepository userRepository, CollegeRepository collegeRepository, ClassroomRepository classroomRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
        this.classroomRepository = classroomRepository;
        this.emailService = emailService;
    }


    @Override
    public ExcelPreviewResponse previewStudentImport(MultipartFile file) {
        return parseExcelInMemory(file, true);
    }

    @Override
    public ExcelPreviewResponse previewTeacherImport(MultipartFile file) {
        return parseExcelInMemory(file, false);
    }

    private ExcelPreviewResponse parseExcelInMemory(MultipartFile file, boolean isStudent) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded Excel file cannot be empty.");
        }

        List<ExcelPreviewResponse.RowItem> rows = new ArrayList<>();
        int validCount = 0;
        int invalidCount = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            int index = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String fullName = getCellValueAsString(row.getCell(0));
                String email = getCellValueAsString(row.getCell(1));
                String rollNumber = isStudent ? getCellValueAsString(row.getCell(2)) : null;
                String phone = isStudent ? getCellValueAsString(row.getCell(3)) : getCellValueAsString(row.getCell(2));

                if ((fullName == null || fullName.isBlank()) && (email == null || email.isBlank())) {
                    continue;
                }

                Map<String, String> errors = new HashMap<>();
                if (fullName == null || fullName.isBlank()) {
                    errors.put("fullName", "Full name is required");
                }
                if (email == null || email.isBlank()) {
                    errors.put("email", "Email is required");
                } else if (!email.contains("@") || !email.contains(".")) {
                    errors.put("email", "Invalid email format");
                } else if (userRepository.existsByEmail(email)) {
                    errors.put("email", "Email already exists in system");
                }

                boolean valid = errors.isEmpty();
                if (valid) validCount++; else invalidCount++;

                rows.add(ExcelPreviewResponse.RowItem.builder()
                        .rowIndex(index++)
                        .fullName(fullName)
                        .email(email)
                        .rollNumber(rollNumber)
                        .phoneNumber(phone)
                        .valid(valid)
                        .errors(errors)
                        .build());
            }

        } catch (Exception e) {
            log.error("Failed to parse Excel file in-memory: {}", e.getMessage());
            throw new BadRequestException("Failed to parse Excel spreadsheet: " + e.getMessage());
        }

        return ExcelPreviewResponse.builder()
                .totalRows(rows.size())
                .validRows(validCount)
                .invalidRows(invalidCount)
                .rows(rows)
                .build();
    }

    @Override
    @Transactional
    public int confirmStudentImport(Long collegeId, ExcelImportConfirmRequest request) {
        return processConfirmedImport(collegeId, request, UserRole.STUDENT);
    }

    @Override
    @Transactional
    public int confirmTeacherImport(Long collegeId, ExcelImportConfirmRequest request) {
        return processConfirmedImport(collegeId, request, UserRole.TEACHER);
    }

    private int processConfirmedImport(Long collegeId, ExcelImportConfirmRequest request, UserRole role) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + collegeId));

        Classroom classroom = null;
        if (request.getClassroomId() != null) {
            classroom = classroomRepository.findById(request.getClassroomId())
                    .orElse(null);
        }

        List<User> usersToSave = new ArrayList<>();
        List<String[]> credentialsToEmail = new ArrayList<>();

        for (ExcelImportConfirmRequest.UserRowItem item : request.getUsers()) {
            if (userRepository.existsByEmail(item.getEmail())) {
                continue;
            }

            String password = (item.getInitialPassword() != null && !item.getInitialPassword().isBlank())
                    ? item.getInitialPassword()
                    : UUID.randomUUID().toString().substring(0, 8);

            User user = User.builder()
                    .fullName(item.getFullName())
                    .email(item.getEmail())
                    .password(password)
                    .role(role)
                    .status(AccountStatus.ACTIVE)
                    .college(college)
                    .classroom(classroom)
                    .rollNumber(item.getRollNumber())
                    .phoneNumber(item.getPhoneNumber())
                    .build();

            usersToSave.add(user);
            credentialsToEmail.add(new String[]{item.getEmail(), item.getFullName(), password});
        }

        userRepository.saveAll(usersToSave);

        for (String[] cred : credentialsToEmail) {
            emailService.sendCredentialsEmail(cred[0], cred[1], cred[2], role.name());
        }

        return usersToSave.size();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
