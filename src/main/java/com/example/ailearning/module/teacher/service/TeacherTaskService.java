package com.example.ailearning.module.teacher.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.teacher.dto.TeacherTaskRequest;
import com.example.ailearning.module.teacher.entity.Teacher;
import com.example.ailearning.module.teacher.entity.TeacherTask;
import com.example.ailearning.module.teacher.entity.TeacherTaskTarget;
import com.example.ailearning.module.teacher.mapper.TeacherMapper;
import com.example.ailearning.module.teacher.mapper.TeacherTaskMapper;
import com.example.ailearning.module.teacher.mapper.TeacherTaskTargetMapper;
import com.example.ailearning.module.teacher.vo.TeacherTaskTargetVO;
import com.example.ailearning.module.teacher.vo.TeacherTaskVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeacherTaskService {
    private final TeacherMapper teacherMapper;
    private final TeacherTaskMapper taskMapper;
    private final TeacherTaskTargetMapper targetMapper;

    public TeacherTaskService(TeacherMapper teacherMapper, TeacherTaskMapper taskMapper, TeacherTaskTargetMapper targetMapper) {
        this.teacherMapper = teacherMapper;
        this.taskMapper = taskMapper;
        this.targetMapper = targetMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public TeacherTaskVO create(TeacherTaskRequest request) {
        Long userId = CurrentUserHolder.getRequired().getUserId();
        Teacher teacher = currentTeacher(userId);
        TeacherTask task = new TeacherTask();
        task.setTeacherId(teacher.getId());
        task.setTaskTitle(request.getTaskTitle());
        task.setTaskType(request.getTaskType());
        task.setTaskDescription(request.getTaskDescription());
        task.setDueAt(request.getDueAt());
        task.setPublishStatus("draft");
        task.setStatus("draft");
        task.setCreatedBy(userId);
        taskMapper.insert(task);

        if (request.getTargetIds() != null) {
            for (Long targetId : request.getTargetIds()) {
                TeacherTaskTarget target = new TeacherTaskTarget();
                target.setTaskId(task.getId());
                target.setTargetType(request.getTargetType());
                target.setTargetId(targetId);
                target.setCompletionStatus("not_started");
                target.setStatus("active");
                target.setCreatedBy(userId);
                targetMapper.insert(target);
            }
        }
        return toVO(task, true);
    }

    public List<TeacherTaskVO> listMine(String publishStatus) {
        Teacher teacher = currentTeacher(CurrentUserHolder.getRequired().getUserId());
        LambdaQueryWrapper<TeacherTask> wrapper = new LambdaQueryWrapper<TeacherTask>()
                .eq(TeacherTask::getTeacherId, teacher.getId())
                .isNull(TeacherTask::getDeletedAt)
                .orderByDesc(TeacherTask::getCreatedAt);
        if (publishStatus != null && !publishStatus.isBlank()) {
            wrapper.eq(TeacherTask::getPublishStatus, publishStatus);
        }
        return taskMapper.selectList(wrapper).stream().map(task -> toVO(task, false)).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public TeacherTaskVO publish(Long id) {
        TeacherTask task = ownedTask(id);
        task.setPublishStatus("published");
        task.setStatus("published");
        task.setPublishedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        return toVO(task, true);
    }

    private TeacherTask ownedTask(Long id) {
        Teacher teacher = currentTeacher(CurrentUserHolder.getRequired().getUserId());
        TeacherTask task = taskMapper.selectById(id);
        if (task == null || task.getDeletedAt() != null || !teacher.getId().equals(task.getTeacherId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教师任务不存在");
        }
        return task;
    }

    private Teacher currentTeacher(Long userId) {
        Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getUserId, userId)
                .isNull(Teacher::getDeletedAt)
                .last("LIMIT 1"));
        if (teacher == null) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "当前账号未绑定教师信息");
        }
        return teacher;
    }

    private TeacherTaskVO toVO(TeacherTask task, boolean includeTargets) {
        TeacherTaskVO vo = new TeacherTaskVO();
        vo.setId(task.getId());
        vo.setTeacherId(task.getTeacherId());
        vo.setTaskTitle(task.getTaskTitle());
        vo.setTaskType(task.getTaskType());
        vo.setTaskDescription(task.getTaskDescription());
        vo.setDueAt(task.getDueAt());
        vo.setPublishStatus(task.getPublishStatus());
        vo.setStatus(task.getStatus());
        if (includeTargets) {
            vo.setTargets(targetMapper.selectList(new LambdaQueryWrapper<TeacherTaskTarget>()
                            .eq(TeacherTaskTarget::getTaskId, task.getId())
                            .isNull(TeacherTaskTarget::getDeletedAt)
                            .orderByAsc(TeacherTaskTarget::getId))
                    .stream().map(this::toTargetVO).toList());
        }
        return vo;
    }

    private TeacherTaskTargetVO toTargetVO(TeacherTaskTarget target) {
        TeacherTaskTargetVO vo = new TeacherTaskTargetVO();
        vo.setId(target.getId());
        vo.setTargetType(target.getTargetType());
        vo.setTargetId(target.getTargetId());
        vo.setCompletionStatus(target.getCompletionStatus());
        return vo;
    }
}
