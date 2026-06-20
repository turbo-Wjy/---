package com.example.ailearning.module.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.mapper.StudentMapper;
import com.example.ailearning.module.teacher.entity.Teacher;
import com.example.ailearning.module.teacher.entity.TeacherStudentGroup;
import com.example.ailearning.module.teacher.mapper.TeacherMapper;
import com.example.ailearning.module.teacher.mapper.TeacherStudentGroupMapper;
import org.springframework.stereotype.Service;

@Service
public class StudentContextService {
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final TeacherStudentGroupMapper teacherStudentGroupMapper;

    public StudentContextService(
            StudentMapper studentMapper,
            TeacherMapper teacherMapper,
            TeacherStudentGroupMapper teacherStudentGroupMapper
    ) {
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
        this.teacherStudentGroupMapper = teacherStudentGroupMapper;
    }

    public Student currentStudentRequired() {
        Student student = currentStudent();
        if (student == null) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "当前账号未绑定学生信息");
        }
        return student;
    }

    public Long currentStudentIdRequired() {
        return currentStudentRequired().getId();
    }

    public Long resolveStudentId(Long requestedStudentId) {
        Student currentStudent = currentStudent();
        if (currentStudent != null) {
            if (requestedStudentId != null && !requestedStudentId.equals(currentStudent.getId())) {
                throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "学生只能操作自己的数据");
            }
            return currentStudent.getId();
        }
        if (requestedStudentId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "非学生账号需要指定 studentId");
        }
        return requestedStudentId;
    }

    public Student currentStudent() {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        return studentMapper.selectOne(new LambdaQueryWrapper<Student>()
                .eq(Student::getUserId, currentUser.getUserId())
                .isNull(Student::getDeletedAt)
                .last("LIMIT 1"));
    }

    public void checkCanViewStudent(Long studentId) {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        if (currentUser.getRoleCodes().stream().anyMatch(role -> role.equals("admin") || role.equals("major_leader") || role.equals("data_viewer"))) {
            return;
        }
        Student currentStudent = currentStudent();
        if (currentStudent != null && currentStudent.getId().equals(studentId)) {
            return;
        }
        Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getUserId, currentUser.getUserId())
                .isNull(Teacher::getDeletedAt)
                .last("LIMIT 1"));
        if (teacher == null) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "当前账号无权查看该学生数据");
        }
        boolean assigned = teacherStudentGroupMapper.exists(new LambdaQueryWrapper<TeacherStudentGroup>()
                .eq(TeacherStudentGroup::getTeacherId, teacher.getId())
                .eq(TeacherStudentGroup::getStudentId, studentId)
                .isNull(TeacherStudentGroup::getDeletedAt));
        if (!assigned) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能查看自己负责学生的数据");
        }
    }
}
