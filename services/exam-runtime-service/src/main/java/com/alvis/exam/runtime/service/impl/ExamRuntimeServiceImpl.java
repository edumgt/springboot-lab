package com.alvis.exam.runtime.service.impl;

import com.alvis.exam.common.exception.BusinessException;
import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.runtime.client.ExamPaperClient;
import com.alvis.exam.runtime.domain.ExamPaperAnswer;
import com.alvis.exam.runtime.domain.ExamPaperQuestionCustomerAnswer;
import com.alvis.exam.runtime.domain.enums.ExamPaperAnswerStatusEnum;
import com.alvis.exam.runtime.domain.enums.QuestionTypeEnum;
import com.alvis.exam.runtime.dto.AnswerPageRequest;
import com.alvis.exam.runtime.dto.AnswerResponse;
import com.alvis.exam.runtime.dto.ExamPaperSubmitItemRequest;
import com.alvis.exam.runtime.dto.ExamPaperSubmitRequest;
import com.alvis.exam.runtime.event.GradingCompleteEvent;
import com.alvis.exam.runtime.repository.ExamPaperAnswerMapper;
import com.alvis.exam.runtime.repository.ExamPaperQuestionCustomerAnswerMapper;
import com.alvis.exam.runtime.service.ExamRuntimeService;
import com.alvis.exam.runtime.util.ExamUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExamRuntimeServiceImpl implements ExamRuntimeService {
    private final ExamPaperClient examPaperClient;
    private final ExamPaperAnswerMapper examPaperAnswerMapper;
    private final ExamPaperQuestionCustomerAnswerMapper customerAnswerMapper;
    private final KafkaTemplate<String, GradingCompleteEvent> kafkaTemplate;

    public ExamRuntimeServiceImpl(ExamPaperClient examPaperClient, ExamPaperAnswerMapper examPaperAnswerMapper,
                                  ExamPaperQuestionCustomerAnswerMapper customerAnswerMapper,
                                  KafkaTemplate<String, GradingCompleteEvent> kafkaTemplate) {
        this.examPaperClient = examPaperClient;
        this.examPaperAnswerMapper = examPaperAnswerMapper;
        this.customerAnswerMapper = customerAnswerMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public AnswerResponse calculateAndSave(ExamPaperSubmitRequest submitRequest, Integer userId) {
        ExamPaperClient.ExamPaperVm examPaper = examPaperClient.getExamPaperVm(submitRequest.getExamPaperId());
        if (examPaper == null) {
            throw new BusinessException("Exam paper not found");
        }
        Map<Integer, ExamPaperSubmitItemRequest> submittedMap = submitRequest.getAnswerItems().stream()
                .collect(Collectors.toMap(ExamPaperSubmitItemRequest::getQuestionId, Function.identity(), (a, b) -> b));
        Date now = new Date();
        List<ExamPaperQuestionCustomerAnswer> customerAnswers = new ArrayList<>();
        int systemScore = 0;
        int correctCount = 0;
        for (ExamPaperClient.TitleVm title : examPaper.getTitleItems()) {
            for (ExamPaperClient.QuestionVm question : title.getQuestionItems()) {
                ExamPaperSubmitItemRequest submitted = submittedMap.get(question.getId());
                ExamPaperQuestionCustomerAnswer answer = new ExamPaperQuestionCustomerAnswer();
                answer.setQuestionId(question.getId());
                answer.setQuestionScore(question.getScore());
                answer.setSubjectId(question.getSubjectId());
                answer.setCreateTime(now);
                answer.setCreateUser(userId);
                answer.setExamPaperId(examPaper.getId());
                answer.setQuestionType(question.getQuestionType());
                boolean doRight = false;
                int customerScore = 0;
                String normalized = null;
                if (submitted != null) {
                    if (QuestionTypeEnum.MultipleChoice.getCode() == question.getQuestionType()) {
                        normalized = ExamUtil.contentToString(submitted.getContentArray());
                        doRight = normalized.equals(question.getCorrect());
                    } else if (QuestionTypeEnum.SingleChoice.getCode() == question.getQuestionType() || QuestionTypeEnum.TrueFalse.getCode() == question.getQuestionType()) {
                        normalized = submitted.getContent();
                        doRight = normalized != null && normalized.equals(question.getCorrect());
                    } else {
                        normalized = submitted.getContent() != null ? submitted.getContent() : ExamUtil.contentToString(submitted.getContentArray());
                    }
                }
                if (doRight) {
                    customerScore = question.getScore() == null ? 0 : question.getScore();
                    correctCount++;
                }
                answer.setAnswer(normalized);
                answer.setDoRight(doRight);
                answer.setCustomerScore(customerScore);
                customerAnswers.add(answer);
                systemScore += customerScore;
            }
        }
        ExamPaperAnswer answer = new ExamPaperAnswer();
        answer.setExamPaperId(examPaper.getId());
        answer.setCreateUser(userId);
        answer.setCreateTime(now);
        answer.setUserScore(systemScore);
        answer.setSubjectId(examPaper.getSubjectId());
        answer.setQuestionCount(examPaper.getQuestionCount());
        answer.setQuestionCorrect(correctCount);
        answer.setPaperScore(examPaper.getScore());
        answer.setDoTime(submitRequest.getDoTime());
        answer.setPaperType(examPaper.getPaperType());
        answer.setSystemScore(systemScore);
        answer.setStatus(ExamPaperAnswerStatusEnum.Complete.getCode());
        answer.setPaperName(examPaper.getName());
        examPaperAnswerMapper.insertSelective(answer);
        customerAnswers.forEach(item -> item.setExamPaperAnswerId(answer.getId()));
        if (!customerAnswers.isEmpty()) {
            customerAnswerMapper.insertList(customerAnswers);
        }
        kafkaTemplate.send("exam.grading.complete", new GradingCompleteEvent(answer.getId(), userId, systemScore));
        return getAnswerDetail(answer.getId());
    }

    @Override
    public RestPage<AnswerResponse> getAnswerPage(AnswerPageRequest pageRequest) {
        PageInfo<ExamPaperAnswer> pageInfo = PageHelper.startPage(pageRequest.getPageIndex(), pageRequest.getPageSize(), "id desc")
                .doSelectPageInfo(() -> examPaperAnswerMapper.studentPage(pageRequest));
        return new RestPage<>(pageInfo.getList().stream().map(this::toResponse).toList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages());
    }

    @Override
    public AnswerResponse getAnswerDetail(Integer id) {
        ExamPaperAnswer answer = examPaperAnswerMapper.selectById(id);
        if (answer == null) {
            throw new BusinessException("Answer not found");
        }
        AnswerResponse response = toResponse(answer);
        response.setAnswerItems(customerAnswerMapper.selectListByPaperAnswerId(id).stream().map(item -> {
            ExamPaperSubmitItemRequest request = new ExamPaperSubmitItemRequest();
            request.setQuestionId(item.getQuestionId());
            request.setContent(item.getAnswer());
            request.setContentArray(ExamUtil.contentToArray(item.getAnswer()));
            return request;
        }).toList());
        return response;
    }

    private AnswerResponse toResponse(ExamPaperAnswer answer) {
        AnswerResponse response = new AnswerResponse();
        response.setId(answer.getId());
        response.setExamPaperId(answer.getExamPaperId());
        response.setCreateUser(answer.getCreateUser());
        response.setCreateTime(answer.getCreateTime());
        response.setUserScore(answer.getUserScore());
        response.setSubjectId(answer.getSubjectId());
        response.setQuestionCount(answer.getQuestionCount());
        response.setQuestionCorrect(answer.getQuestionCorrect());
        response.setPaperScore(answer.getPaperScore());
        response.setDoTime(answer.getDoTime());
        response.setPaperType(answer.getPaperType());
        response.setSystemScore(answer.getSystemScore());
        response.setStatus(answer.getStatus());
        response.setPaperName(answer.getPaperName());
        return response;
    }
}
