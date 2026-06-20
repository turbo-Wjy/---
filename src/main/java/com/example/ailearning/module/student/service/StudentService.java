package com.example.ailearning.module.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.role.entity.Role;
import com.example.ailearning.module.role.mapper.RoleMapper;
import com.example.ailearning.module.student.dto.StudentPageQuery;
import com.example.ailearning.module.student.dto.StudentRequest;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.mapper.StudentMapper;
import com.example.ailearning.module.student.vo.StudentVO;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.entity.UserRole;
import com.example.ailearning.module.user.mapper.UserMapper;
import com.example.ailearning.module.user.mapper.UserRoleMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
            StudentMapper studentMapper,
            UserMapper userMapper,
            UserRoleMapper userRoleMapper,
            RoleMapper roleMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.studentMapper = studentMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResult<StudentVO> page(StudentPageQuery query) {
        Page<Student> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<Student>()
                .isNull(Student::getDeletedAt)
                .orderByDesc(Student::getGrade)
                .orderByAsc(Student::getStudentNo);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Student::getStudentNo, query.getKeyword());
        }
        if (query.getMajorId() != null) {
            wrapper.eq(Student::getMajorId, query.getMajorId());
        }
        if (query.getClassId() != null) {
            wrapper.eq(Student::getClassId, query.getClassId());
        }
        if (query.getGrade() != null && !query.getGrade().isBlank()) {
            wrapper.eq(Student::getGrade, query.getGrade());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(Student::getStatus, query.getStatus());
        }
        Page<Student> result = studentMapper.selectPage(page, wrapper);
        List<StudentVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public StudentVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public StudentVO create(StudentRequest request) {
        boolean exists = studentMapper.exists(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, request.getStudentNo())
                .isNull(Student::getDeletedAt));
        if (exists) {
            throw new BusinessException(ErrorCode.CONFLICT, "学号已存在");
        }
        boolean userExists = userMapper.exists(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getStudentNo())
                .isNull(User::getDeletedAt));
        if (userExists) {
            throw new BusinessException(ErrorCode.CONFLICT, "登录账号已存在");
        }

        Role studentRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, "student")
                .isNull(Role::getDeletedAt)
                .last("LIMIT 1"));
        if (studentRole == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "学生角色不存在，请先执行 seed.sql");
        }

        User user = new User();
        user.setUsername(request.getStudentNo());
        user.setRealName(request.getRealName());
        user.setPasswordHash(passwordEncoder.encode(defaultInitialPassword(request.getStudentNo())));
        user.setAccountStatus("active");
        user.setMustChangePassword(true);
        userMapper.insert(user);

        Student student = new Student();
        fill(student, request);
        student.setUserId(user.getId());
        studentMapper.insert(student);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(studentRole.getId());
        userRoleMapper.insert(userRole);
        return toVO(student);
    }

    @Transactional(rollbackFor = Exception.class)
    public StudentVO update(Long id, StudentRequest request) {
        Student student = getEntity(id);
        fill(student, request);
        studentMapper.updateById(student);

        User user = userMapper.selectById(student.getUserId());
        if (user != null) {
            user.setRealName(request.getRealName());
            userMapper.updateById(user);
        }
        return toVO(student);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Student student = getEntity(id);
        student.setDeletedAt(DeleteConstants.now());
        student.setStatus("deleted");
        studentMapper.updateById(student);

        User user = userMapper.selectById(student.getUserId());
        if (user != null) {
            user.setAccountStatus("disabled");
            userMapper.updateById(user);
        }
    }

    private Student getEntity(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null || student.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生不存在");
        }
        return student;
    }

    private void fill(Student student, StudentRequest request) {
        student.setStudentNo(request.getStudentNo());
        student.setCollegeId(request.getCollegeId());
        student.setMajorId(request.getMajorId());
        student.setClassId(request.getClassId());
        student.setGrade(request.getGrade());
        student.setGender(request.getGender());
        student.setEnrollmentStatus(request.getEnrollmentStatus());
        student.setStatus(request.getStatus());
    }

    private StudentVO toVO(Student student) {
        StudentVO vo = new StudentVO();
        vo.setId(student.getId());
        vo.setUserId(student.getUserId());
        vo.setStudentNo(student.getStudentNo());
        vo.setCollegeId(student.getCollegeId());
        vo.setMajorId(student.getMajorId());
        vo.setClassId(student.getClassId());
        vo.setGrade(student.getGrade());
        vo.setGender(student.getGender());
        vo.setEnrollmentStatus(student.getEnrollmentStatus());
        vo.setStatus(student.getStatus());

        User user = userMapper.selectById(student.getUserId());
        if (user != null) {
            vo.setRealName(user.getRealName());
        }
        return vo;
    }

    private String defaultInitialPassword(String studentNo) {
        return studentNo + "@123456";
    }
}
