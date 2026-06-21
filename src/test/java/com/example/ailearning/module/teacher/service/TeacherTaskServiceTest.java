package com.example.ailearning.module.teacher.service;

import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.module.teacher.dto.TeacherTaskRequest;
import com.example.ailearning.module.teacher.entity.Teacher;
import com.example.ailearning.module.teacher.entity.TeacherTask;
import com.example.ailearning.module.teacher.mapper.TeacherMapper;
import com.example.ailearning.module.teacher.mapper.TeacherTaskMapper;
import com.example.ailearning.module.teacher.mapper.TeacherTaskTargetMapper;
import com.example.ailearning.module.teacher.vo.TeacherTaskVO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TeacherTaskServiceTest {

    @Test
    void createTaskCreatesDraftForCurrentTeacher() {
        TeacherTaskMapper taskMapper = mock(TeacherTaskMapper.class);
        TeacherTaskService service = service(taskMapper);
        setUser();

        TeacherTaskRequest request = new TeacherTaskRequest();
        request.setTaskTitle("完成项目周报");
        request.setTaskType("project");
        request.setTargetType("class");
        request.setTargetIds(List.of(1L, 2L));

        TeacherTaskVO result = service.create(request);

        ArgumentCaptor<TeacherTask> captor = ArgumentCaptor.forClass(TeacherTask.class);
        verify(taskMapper).insert(captor.capture());
        assertThat(captor.getValue().getPublishStatus()).isEqualTo("draft");
        assertThat(result.getTaskTitle()).isEqualTo("完成项目周报");
        SecurityContextHolder.clearContext();
    }

    private TeacherTaskService service(TeacherTaskMapper taskMapper) {
        Teacher teacher = new Teacher();
        teacher.setId(9L);
        TeacherMapper teacherMapper = mock(TeacherMapper.class);
        when(teacherMapper.selectOne(any())).thenReturn(teacher);
        return new TeacherTaskService(teacherMapper, taskMapper, mock(TeacherTaskTargetMapper.class));
    }

    private void setUser() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new CurrentUser(7L, "teacher01", "教师一", List.of("teacher"), List.of("teacher_dashboard.view.assigned")),
                null
        ));
    }
}
