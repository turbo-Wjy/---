package com.example.ailearning.module.student.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.module.base.entity.Major;
import com.example.ailearning.module.base.entity.SchoolClass;
import com.example.ailearning.module.base.mapper.MajorMapper;
import com.example.ailearning.module.base.mapper.SchoolClassMapper;
import com.example.ailearning.module.role.entity.Role;
import com.example.ailearning.module.role.mapper.RoleMapper;
import com.example.ailearning.module.student.dto.StudentImportConfirmRequest;
import com.example.ailearning.module.student.dto.StudentImportRow;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.mapper.StudentMapper;
import com.example.ailearning.module.student.vo.StudentImportConfirmVO;
import com.example.ailearning.module.student.vo.StudentImportPreviewVO;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.entity.UserRole;
import com.example.ailearning.module.user.mapper.UserMapper;
import com.example.ailearning.module.user.mapper.UserRoleMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StudentImportService {
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final MajorMapper majorMapper;
    private final SchoolClassMapper schoolClassMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public StudentImportService(
            StudentMapper studentMapper,
            UserMapper userMapper,
            UserRoleMapper userRoleMapper,
            MajorMapper majorMapper,
            SchoolClassMapper schoolClassMapper,
            RoleMapper roleMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.studentMapper = studentMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.majorMapper = majorMapper;
        this.schoolClassMapper = schoolClassMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public StudentImportPreviewVO preview(MultipartFile file) {
        List<Map<Integer, String>> excelRows;
        try {
            excelRows = EasyExcel.read(file.getInputStream()).sheet().headRowNumber(1).doReadSync();
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "Excel 文件读取失败");
        }

        List<StudentImportRow> rows = new ArrayList<>();
        Set<String> seenStudentNos = new LinkedHashSet<>();
        for (int i = 0; i < excelRows.size(); i++) {
            Map<Integer, String> raw = excelRows.get(i);
            StudentImportRow row = new StudentImportRow();
            row.setRowIndex(i + 2);
            row.setStudentNo(value(raw, 0));
            row.setRealName(value(raw, 1));
            row.setMajorName(value(raw, 2));
            row.setClassName(value(raw, 3));
            row.setGrade(value(raw, 4));
            row.setGender(value(raw, 5));
            validateRow(row, seenStudentNos);
            rows.add(row);
        }

        StudentImportPreviewVO vo = new StudentImportPreviewVO();
        vo.setRows(rows);
        vo.setTotalCount(rows.size());
        vo.setValidCount((int) rows.stream().filter(row -> Boolean.TRUE.equals(row.getValid())).count());
        vo.setInvalidCount(vo.getTotalCount() - vo.getValidCount());
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public StudentImportConfirmVO confirm(StudentImportConfirmRequest request) {
        Role studentRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, "student")
                .isNull(Role::getDeletedAt)
                .last("LIMIT 1"));
        if (studentRole == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "学生角色不存在，请先执行 seed.sql");
        }

        int imported = 0;
        int skipped = 0;
        for (StudentImportRow row : request.getRows()) {
            validateRow(row, new LinkedHashSet<>());
            if (!Boolean.TRUE.equals(row.getValid())) {
                skipped++;
                continue;
            }
            boolean exists = studentMapper.exists(new LambdaQueryWrapper<Student>()
                    .eq(Student::getStudentNo, row.getStudentNo())
                    .isNull(Student::getDeletedAt));
            if (exists) {
                skipped++;
                continue;
            }

            User user = new User();
            user.setUsername(row.getStudentNo());
            user.setRealName(row.getRealName());
            user.setPasswordHash(passwordEncoder.encode(defaultInitialPassword(row.getStudentNo())));
            user.setAccountStatus("active");
            user.setMustChangePassword(true);
            userMapper.insert(user);

            Student student = new Student();
            student.setUserId(user.getId());
            student.setStudentNo(row.getStudentNo());
            student.setCollegeId(row.getCollegeId());
            student.setMajorId(row.getMajorId());
            student.setClassId(row.getClassId());
            student.setGrade(row.getGrade());
            student.setGender(row.getGender());
            student.setEnrollmentStatus("studying");
            student.setStatus("active");
            studentMapper.insert(student);

            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(studentRole.getId());
            userRoleMapper.insert(userRole);
            imported++;
        }

        StudentImportConfirmVO vo = new StudentImportConfirmVO();
        vo.setImportedCount(imported);
        vo.setSkippedCount(skipped);
        return vo;
    }

    private void validateRow(StudentImportRow row, Set<String> seenStudentNos) {
        List<String> errors = new ArrayList<>();
        if (blank(row.getStudentNo())) {
            errors.add("学号不能为空");
        }
        if (blank(row.getRealName())) {
            errors.add("姓名不能为空");
        }
        if (blank(row.getMajorName())) {
            errors.add("专业不能为空");
        }
        if (blank(row.getClassName())) {
            errors.add("班级不能为空");
        }
        if (blank(row.getGrade())) {
            errors.add("年级不能为空");
        }
        if (!blank(row.getStudentNo()) && !seenStudentNos.add(row.getStudentNo())) {
            errors.add("导入文件内学号重复");
        }

        Major major = null;
        if (!blank(row.getMajorName())) {
            major = majorMapper.selectOne(new LambdaQueryWrapper<Major>()
                    .eq(Major::getName, row.getMajorName())
                    .isNull(Major::getDeletedAt)
                    .last("LIMIT 1"));
            if (major == null) {
                errors.add("专业不存在");
            } else {
                row.setMajorId(major.getId());
                row.setCollegeId(major.getCollegeId());
            }
        }

        if (major != null && !blank(row.getClassName())) {
            SchoolClass schoolClass = schoolClassMapper.selectOne(new LambdaQueryWrapper<SchoolClass>()
                    .eq(SchoolClass::getMajorId, major.getId())
                    .eq(SchoolClass::getName, row.getClassName())
                    .isNull(SchoolClass::getDeletedAt)
                    .last("LIMIT 1"));
            if (schoolClass == null) {
                errors.add("班级不存在或不属于该专业");
            } else {
                row.setClassId(schoolClass.getId());
            }
        }

        if (!blank(row.getStudentNo())) {
            boolean exists = studentMapper.exists(new LambdaQueryWrapper<Student>()
                    .eq(Student::getStudentNo, row.getStudentNo())
                    .isNull(Student::getDeletedAt));
            if (exists) {
                errors.add("学号已存在");
            }
        }

        row.setValid(errors.isEmpty());
        row.setErrorMessage(String.join("；", errors));
    }

    private String defaultInitialPassword(String studentNo) {
        return studentNo + "@123456";
    }

    private String value(Map<Integer, String> row, int index) {
        String value = row.get(index);
        return value == null ? null : value.trim();
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
