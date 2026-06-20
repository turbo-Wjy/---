package com.example.ailearning.module.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.course.dto.QuizAttemptRequest;
import com.example.ailearning.module.course.entity.QuizAttempt;
import com.example.ailearning.module.course.entity.WrongQuestion;
import com.example.ailearning.module.course.mapper.QuizAttemptMapper;
import com.example.ailearning.module.course.mapper.WrongQuestionMapper;
import com.example.ailearning.module.course.vo.QuizAttemptVO;
import com.example.ailearning.module.course.vo.WrongQuestionVO;
import com.example.ailearning.module.student.service.StudentContextService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizAttemptService {
    private final CourseService courseService;
    private final KnowledgePointService knowledgePointService;
    private final StudentContextService studentContextService;
    private final QuizAttemptMapper quizAttemptMapper;
    private final WrongQuestionMapper wrongQuestionMapper;

    public QuizAttemptService(
            CourseService courseService,
            KnowledgePointService knowledgePointService,
            StudentContextService studentContextService,
            QuizAttemptMapper quizAttemptMapper,
            WrongQuestionMapper wrongQuestionMapper
    ) {
        this.courseService = courseService;
        this.knowledgePointService = knowledgePointService;
        this.studentContextService = studentContextService;
        this.quizAttemptMapper = quizAttemptMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public QuizAttemptVO create(QuizAttemptRequest request) {
        courseService.getEntity(request.getCourseId());
        if (request.getKnowledgePointId() != null) {
            knowledgePointService.getEntity(request.getKnowledgePointId());
        }
        Long studentId = studentContextService.resolveStudentId(request.getStudentId());
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudentId(studentId);
        attempt.setCourseId(request.getCourseId());
        attempt.setKnowledgePointId(request.getKnowledgePointId());
        attempt.setQuestionSnapshot(request.getQuestionSnapshot());
        attempt.setAnswer(request.getAnswer());
        attempt.setCorrect(request.getCorrect());
        attempt.setScore(request.getScore());
        attempt.setStatus("active");
        attempt.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        quizAttemptMapper.insert(attempt);

        if (Boolean.FALSE.equals(request.getCorrect())) {
            WrongQuestion wrongQuestion = new WrongQuestion();
            wrongQuestion.setStudentId(studentId);
            wrongQuestion.setQuizAttemptId(attempt.getId());
            wrongQuestion.setKnowledgePointId(request.getKnowledgePointId());
            wrongQuestion.setWrongReason(request.getWrongReason());
            wrongQuestion.setReviewStatus("unreviewed");
            wrongQuestion.setStatus("active");
            wrongQuestion.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
            wrongQuestionMapper.insert(wrongQuestion);
        }
        return toVO(attempt);
    }

    public List<WrongQuestionVO> myWrongQuestions(Long knowledgePointId) {
        Long studentId = studentContextService.currentStudentIdRequired();
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getStudentId, studentId)
                .isNull(WrongQuestion::getDeletedAt)
                .orderByDesc(WrongQuestion::getCreatedAt);
        if (knowledgePointId != null) {
            wrapper.eq(WrongQuestion::getKnowledgePointId, knowledgePointId);
        }
        return wrongQuestionMapper.selectList(wrapper).stream().map(this::toWrongQuestionVO).toList();
    }

    private QuizAttemptVO toVO(QuizAttempt attempt) {
        QuizAttemptVO vo = new QuizAttemptVO();
        vo.setId(attempt.getId());
        vo.setStudentId(attempt.getStudentId());
        vo.setCourseId(attempt.getCourseId());
        vo.setKnowledgePointId(attempt.getKnowledgePointId());
        vo.setCorrect(attempt.getCorrect());
        vo.setScore(attempt.getScore());
        return vo;
    }

    private WrongQuestionVO toWrongQuestionVO(WrongQuestion wrongQuestion) {
        WrongQuestionVO vo = new WrongQuestionVO();
        vo.setId(wrongQuestion.getId());
        vo.setStudentId(wrongQuestion.getStudentId());
        vo.setQuizAttemptId(wrongQuestion.getQuizAttemptId());
        vo.setKnowledgePointId(wrongQuestion.getKnowledgePointId());
        vo.setWrongReason(wrongQuestion.getWrongReason());
        vo.setReviewStatus(wrongQuestion.getReviewStatus());
        vo.setStatus(wrongQuestion.getStatus());
        return vo;
    }
}
