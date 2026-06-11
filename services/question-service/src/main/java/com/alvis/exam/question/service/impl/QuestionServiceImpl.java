package com.alvis.exam.question.service.impl;

import com.alvis.exam.common.exception.BusinessException;
import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.common.util.JsonUtil;
import com.alvis.exam.question.client.SubjectClient;
import com.alvis.exam.question.domain.*;
import com.alvis.exam.question.domain.enums.QuestionStatusEnum;
import com.alvis.exam.question.domain.enums.QuestionTypeEnum;
import com.alvis.exam.question.dto.QuestionEditRequest;
import com.alvis.exam.question.dto.QuestionPageRequest;
import com.alvis.exam.question.dto.QuestionResponse;
import com.alvis.exam.question.repository.QuestionMapper;
import com.alvis.exam.question.repository.TextContentMapper;
import com.alvis.exam.question.service.QuestionService;
import com.alvis.exam.question.util.ExamUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionMapper questionMapper;
    private final TextContentMapper textContentMapper;
    private final SubjectClient subjectClient;

    public QuestionServiceImpl(QuestionMapper questionMapper, TextContentMapper textContentMapper, SubjectClient subjectClient) {
        this.questionMapper = questionMapper;
        this.textContentMapper = textContentMapper;
        this.subjectClient = subjectClient;
    }

    @Override
    public RestPage<QuestionResponse> page(QuestionPageRequest request) {
        PageInfo<Question> pageInfo = PageHelper.startPage(request.getPageIndex(), request.getPageSize(), "id desc")
                .doSelectPageInfo(() -> questionMapper.page(request));
        return new RestPage<>(pageInfo.getList().stream().map(this::toResponse).toList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages());
    }

    @Override
    public QuestionResponse getById(Integer id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException("Question not found");
        }
        return toResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse create(QuestionEditRequest request, Integer userId) {
        Question question = new Question();
        question.setCreateTime(new Date());
        question.setCreateUser(userId);
        question.setStatus(QuestionStatusEnum.OK.getCode());
        apply(question, request);
        questionMapper.insertSelective(question);
        return getById(question.getId());
    }

    @Override
    @Transactional
    public QuestionResponse update(Integer id, QuestionEditRequest request) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException("Question not found");
        }
        apply(question, request);
        question.setId(id);
        questionMapper.updateByPrimaryKeySelective(question);
        return getById(id);
    }

    @Override
    public List<QuestionResponse> getByIds(List<Integer> ids) {
        return questionMapper.selectByIds(ids).stream().map(this::toResponse).toList();
    }

    private void apply(Question question, QuestionEditRequest request) {
        Integer gradeLevel = subjectClient.getLevel(request.getSubjectId());
        QuestionObject object = new QuestionObject();
        object.setTitleContent(request.getTitle());
        object.setAnalyze(request.getAnalyze());
        object.setQuestionItemObjects(request.getItems());
        if (question.getInfoTextContentId() == null) {
            TextContent textContent = new TextContent(JsonUtil.toJsonStr(object), new Date());
            textContentMapper.insertSelective(textContent);
            question.setInfoTextContentId(textContent.getId());
        } else {
            TextContent textContent = textContentMapper.selectById(question.getInfoTextContentId());
            textContent.setContent(JsonUtil.toJsonStr(object));
            textContentMapper.updateByPrimaryKeySelective(textContent);
        }
        question.setQuestionType(request.getQuestionType());
        question.setSubjectId(request.getSubjectId());
        question.setScore(request.getScore());
        question.setDifficult(request.getDifficult());
        question.setGradeLevel(gradeLevel);
        if (request.getQuestionType().equals(QuestionTypeEnum.MultipleChoice.getCode())) {
            question.setCorrect(ExamUtil.contentToString(request.getCorrectArray()));
        } else if ((request.getCorrect() == null || request.getCorrect().isBlank()) && request.getCorrectArray() != null) {
            question.setCorrect(ExamUtil.contentToString(request.getCorrectArray()));
        } else {
            question.setCorrect(request.getCorrect());
        }
    }

    private QuestionResponse toResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestionType(question.getQuestionType());
        response.setCreateTime(question.getCreateTime());
        response.setSubjectId(question.getSubjectId());
        response.setCreateUser(question.getCreateUser());
        response.setScore(question.getScore());
        response.setStatus(question.getStatus());
        response.setCorrect(question.getCorrect());
        response.setDifficult(question.getDifficult());
        response.setInfoTextContentId(question.getInfoTextContentId());
        response.setGradeLevel(question.getGradeLevel());
        TextContent textContent = textContentMapper.selectById(question.getInfoTextContentId());
        if (textContent != null) {
            QuestionObject object = JsonUtil.toJsonObject(textContent.getContent(), QuestionObject.class);
            if (object != null) {
                response.setTitle(object.getTitleContent());
                response.setShortTitle(object.getTitleContent());
                response.setAnalyze(object.getAnalyze());
                response.setItems(object.getQuestionItemObjects());
            }
        }
        response.setCorrectArray(ExamUtil.contentToArray(question.getCorrect()));
        return response;
    }
}
