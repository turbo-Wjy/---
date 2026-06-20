package com.example.ailearning.module.student.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.student.dto.StudentImportConfirmRequest;
import com.example.ailearning.module.student.dto.StudentPageQuery;
import com.example.ailearning.module.student.dto.StudentRequest;
import com.example.ailearning.module.student.service.StudentImportService;
import com.example.ailearning.module.student.service.StudentService;
import com.example.ailearning.module.student.vo.StudentImportConfirmVO;
import com.example.ailearning.module.student.vo.StudentImportPreviewVO;
import com.example.ailearning.module.student.vo.StudentVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;
    private final StudentImportService studentImportService;

    public StudentController(StudentService studentService, StudentImportService studentImportService) {
        this.studentService = studentService;
        this.studentImportService = studentImportService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('student_profile.view_major','student_profile.view_assigned','profile.view.major','profile.view.assigned','account.manage')")
    public ApiResponse<PageResult<StudentVO>> page(StudentPageQuery query) {
        return ApiResponse.success(studentService.page(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('student_profile.view_major','student_profile.view_assigned','profile.view.major','profile.view.assigned','account.manage')")
    public ApiResponse<StudentVO> get(@PathVariable Long id) {
        return ApiResponse.success(studentService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student.import_major')")
    public ApiResponse<StudentVO> create(@Valid @RequestBody StudentRequest request) {
        return ApiResponse.success(studentService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('student.import_major')")
    public ApiResponse<StudentVO> update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ApiResponse.success(studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('student.import_major')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        studentService.softDelete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/import/preview")
    @PreAuthorize("hasAuthority('student.import_major')")
    public ApiResponse<StudentImportPreviewVO> importPreview(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(studentImportService.preview(file));
    }

    @PostMapping("/import/confirm")
    @PreAuthorize("hasAuthority('student.import_major')")
    public ApiResponse<StudentImportConfirmVO> importConfirm(@Valid @RequestBody StudentImportConfirmRequest request) {
        return ApiResponse.success(studentImportService.confirm(request));
    }
}
