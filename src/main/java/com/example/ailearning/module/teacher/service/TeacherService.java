package com.example.ailearning.module.teacher.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.role.entity.Role;
import com.example.ailearning.module.role.mapper.RoleMapper;
import com.example.ailearning.module.teacher.dto.TeacherDutyTagRequest;
import com.example.ailearning.module.teacher.dto.TeacherPageQuery;
import com.example.ailearning.module.teacher.dto.TeacherRequest;
import com.example.ailearning.module.teacher.entity.Teacher;
import com.example.ailearning.module.teacher.entity.TeacherDutyTag;
import com.example.ailearning.module.teacher.mapper.TeacherDutyTagMapper;
import com.example.ailearning.module.teacher.mapper.TeacherMapper;
import com.example.ailearning.module.teacher.vo.TeacherVO;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.entity.UserRole;
import com.example.ailearning.module.user.mapper.UserMapper;
import com.example.ailearning.module.user.mapper.UserRoleMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherService {
    private final TeacherMapper teacherMapper;
    private final TeacherDutyTagMapper teacherDutyTagMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherMapper teacherMapper, TeacherDutyTagMapper teacherDutyTagMapper, UserMapper userMapper,
                          UserRoleMapper userRoleMapper, RoleMapper roleMapper, PasswordEncoder passwordEncoder) {
        this.teacherMapper = teacherMapper;
        this.teacherDutyTagMapper = teacherDutyTagMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResult<TeacherVO> page(TeacherPageQuery query) {
        Page<Teacher> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<Teacher>()
                .isNull(Teacher::getDeletedAt)
                .orderByAsc(Teacher::getTeacherNo);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Teacher::getTeacherNo, query.getKeyword());
        }
        if (query.getCollegeId() != null) {
            wrapper.eq(Teacher::getCollegeId, query.getCollegeId());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(Teacher::getStatus, query.getStatus());
        }
        Page<Teacher> result = teacherMapper.selectPage(page, wrapper);
        List<TeacherVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public TeacherVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public TeacherVO create(TeacherRequest request) {
        if (teacherMapper.exists(new LambdaQueryWrapper<Teacher>().eq(Teacher::getTeacherNo, request.getTeacherNo()).isNull(Teacher::getDeletedAt))) {
            throw new BusinessException(ErrorCode.CONFLICT, "教师工号已存在");
        }
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getTeacherNo()).isNull(User::getDeletedAt))) {
            throw new BusinessException(ErrorCode.CONFLICT, "登录账号已存在");
        }
        Role teacherRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getCode, "teacher").isNull(Role::getDeletedAt).last("LIMIT 1"));
        if (teacherRole == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "教师角色不存在，请先执行 seed.sql");
        }

        User user = new User();
        user.setUsername(request.getTeacherNo());
        user.setRealName(request.getRealName());
        user.setPasswordHash(passwordEncoder.encode(request.getTeacherNo() + "@123456"));
        user.setAccountStatus("active");
        user.setMustChangePassword(true);
        userMapper.insert(user);

        Teacher teacher = new Teacher();
        fill(teacher, request);
        teacher.setUserId(user.getId());
        teacherMapper.insert(teacher);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(teacherRole.getId());
        userRoleMapper.insert(userRole);
        return toVO(teacher);
    }

    @Transactional(rollbackFor = Exception.class)
    public TeacherVO update(Long id, TeacherRequest request) {
        Teacher teacher = getEntity(id);
        fill(teacher, request);
        teacherMapper.updateById(teacher);
        User user = userMapper.selectById(teacher.getUserId());
        if (user != null) {
            user.setRealName(request.getRealName());
            userMapper.updateById(user);
        }
        return toVO(teacher);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Teacher teacher = getEntity(id);
        teacher.setDeletedAt(DeleteConstants.now());
        teacher.setStatus("deleted");
        teacherMapper.updateById(teacher);
        User user = userMapper.selectById(teacher.getUserId());
        if (user != null) {
            user.setAccountStatus("disabled");
            userMapper.updateById(user);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDutyTags(Long teacherId, TeacherDutyTagRequest request) {
        getEntity(teacherId);
        teacherDutyTagMapper.delete(new LambdaQueryWrapper<TeacherDutyTag>().eq(TeacherDutyTag::getTeacherId, teacherId));
        for (TeacherDutyTagRequest.TagItem item : request.getTags()) {
            TeacherDutyTag tag = new TeacherDutyTag();
            tag.setTeacherId(teacherId);
            tag.setTagCode(item.getTagCode());
            tag.setTagName(item.getTagName());
            teacherDutyTagMapper.insert(tag);
        }
    }

    private Teacher getEntity(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null || teacher.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教师不存在");
        }
        return teacher;
    }

    private void fill(Teacher teacher, TeacherRequest request) {
        teacher.setTeacherNo(request.getTeacherNo());
        teacher.setCollegeId(request.getCollegeId());
        teacher.setTitle(request.getTitle());
        teacher.setStatus(request.getStatus());
    }

    private TeacherVO toVO(Teacher teacher) {
        TeacherVO vo = new TeacherVO();
        vo.setId(teacher.getId());
        vo.setUserId(teacher.getUserId());
        vo.setTeacherNo(teacher.getTeacherNo());
        vo.setCollegeId(teacher.getCollegeId());
        vo.setTitle(teacher.getTitle());
        vo.setStatus(teacher.getStatus());
        User user = userMapper.selectById(teacher.getUserId());
        if (user != null) {
            vo.setRealName(user.getRealName());
        }
        List<String> tags = teacherDutyTagMapper.selectList(new LambdaQueryWrapper<TeacherDutyTag>().eq(TeacherDutyTag::getTeacherId, teacher.getId()))
                .stream().map(TeacherDutyTag::getTagCode).toList();
        vo.setDutyTags(tags);
        return vo;
    }
}
