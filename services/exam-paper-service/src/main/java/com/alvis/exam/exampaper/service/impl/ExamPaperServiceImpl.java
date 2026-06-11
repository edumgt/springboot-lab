package com.alvis.exam.exampaper.service.impl;

import com.alvis.exam.common.exception.BusinessException;
import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.common.util.DateTimeUtil;
import com.alvis.exam.common.util.JsonUtil;
import com.alvis.exam.exampaper.client.QuestionClient;
import com.alvis.exam.exampaper.client.SubjectClient;
import com.alvis.exam.exampaper.domain.ExamPaper;
import com.alvis.exam.exampaper.domain.KeyValue;
import com.alvis.exam.exampaper.domain.TextContent;
import com.alvis.exam.exampaper.domain.exam.ExamPaperQuestionItemObject;
import com.alvis.exam.exampaper.domain.exam.ExamPaperTitleItemObject;
import com.alvis.exam.exampaper.dto.*;
import com.alvis.exam.exampaper.repository.ExamPaperMapper;
import com.alvis.exam.exampaper.repository.TextContentMapper;
import com.alvis.exam.exampaper.service.ExamPaperService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExamPaperServiceImpl implements ExamPaperService {
    private final ExamPaperMapper examPaperMapper;
    private final TextContentMapper textContentMapper;
    private final QuestionClient questionClient;
    private final SubjectClient subjectClient;

    public ExamPaperServiceImpl(ExamPaperMapper examPaperMapper, TextContentMapper textContentMapper, QuestionClient questionClient, SubjectClient subjectClient) {
        this.examPaperMapper = examPaperMapper;
        this.textContentMapper = textContentMapper;
        this.questionClient = questionClient;
        this.subjectClient = subjectClient;
    }

    @Override
    public RestPage<ExamPaperResponse> page(ExamPaperPageRequest request) {
        PageInfo<ExamPaper> pageInfo = PageHelper.startPage(request.getPageIndex(), request.getPageSize(), "id desc")
                .doSelectPageInfo(() -> examPaperMapper.page(request));
        return new RestPage<>(pageInfo.getList().stream().map(this::toResponse).toList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages());
    }

    @Override
    public RestPage<ExamPaperResponse> studentPage(ExamPaperPageRequest request) {
        PageInfo<ExamPaper> pageInfo = PageHelper.startPage(request.getPageIndex(), request.getPageSize(), "id desc")
                .doSelectPageInfo(() -> examPaperMapper.studentPage(request));
        return new RestPage<>(pageInfo.getList().stream().map(this::toResponse).toList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages());
    }

    @Override
    public ExamPaperResponse getById(Integer id) {
        ExamPaper examPaper = examPaperMapper.selectById(id);
        if (examPaper == null) {
            throw new BusinessException("Exam paper not found");
        }
        return toResponse(examPaper);
    }

    @Override
    public ExamPaperVM getVm(Integer id) {
        ExamPaper examPaper = examPaperMapper.selectById(id);
        if (examPaper == null) {
            throw new BusinessException("Exam paper not found");
        }
        ExamPaperVM vm = new ExamPaperVM();
        vm.setId(examPaper.getId());
        vm.setName(examPaper.getName());
        vm.setQuestionCount(examPaper.getQuestionCount());
        vm.setScore(examPaper.getScore());
        vm.setSubjectId(examPaper.getSubjectId());
        vm.setPaperType(examPaper.getPaperType());
        vm.setSuggestTime(examPaper.getSuggestTime());
        vm.setLimitStartTime(examPaper.getLimitStartTime());
        vm.setLimitEndTime(examPaper.getLimitEndTime());
        vm.setGradeLevel(examPaper.getGradeLevel());
        TextContent frame = textContentMapper.selectById(examPaper.getFrameTextContentId());
        List<ExamPaperTitleItemObject> titles = JsonUtil.toJsonListObject(frame.getContent(), ExamPaperTitleItemObject.class);
        List<Integer> ids = titles.stream().flatMap(t -> t.getQuestionItems().stream().map(ExamPaperQuestionItemObject::getId)).distinct().toList();
        Map<Integer, QuestionClient.QuestionPayload> questionMap = questionClient.getByIds(ids).stream().collect(Collectors.toMap(QuestionClient.QuestionPayload::getId, Function.identity()));
        vm.setTitleItems(titles.stream().map(title -> {
            ExamPaperVM.TitleVM titleVM = new ExamPaperVM.TitleVM();
            titleVM.setName(title.getName());
            titleVM.setQuestionItems(title.getQuestionItems().stream().map(item -> {
                ExamPaperVM.QuestionVM questionVM = new ExamPaperVM.QuestionVM();
                questionVM.setId(item.getId());
                questionVM.setItemOrder(item.getItemOrder());
                QuestionClient.QuestionPayload payload = questionMap.get(item.getId());
                if (payload != null) {
                    questionVM.setQuestionType(payload.getQuestionType());
                    questionVM.setSubjectId(payload.getSubjectId());
                    questionVM.setScore(payload.getScore());
                    questionVM.setDifficult(payload.getDifficult());
                    questionVM.setGradeLevel(payload.getGradeLevel());
                    questionVM.setCorrect(payload.getCorrect());
                    questionVM.setTitle(payload.getTitle());
                    questionVM.setAnalyze(payload.getAnalyze());
                    questionVM.setCorrectArray(payload.getCorrectArray());
                    if (payload.getItems() != null) {
                        questionVM.setItems(payload.getItems().stream().map(qi -> {
                            ExamPaperVM.ItemVM itemVM = new ExamPaperVM.ItemVM();
                            itemVM.setPrefix(qi.getPrefix());
                            itemVM.setContent(qi.getContent());
                            itemVM.setScore(qi.getScore());
                            return itemVM;
                        }).toList());
                    }
                }
                return questionVM;
            }).toList());
            return titleVM;
        }).toList());
        return vm;
    }

    @Override
    @Transactional
    public ExamPaperResponse create(ExamPaperEditRequest request, Integer userId) {
        ExamPaper examPaper = new ExamPaper();
        examPaper.setCreateTime(new Date());
        examPaper.setCreateUser(userId);
        apply(examPaper, request);
        examPaperMapper.insertSelective(examPaper);
        return getById(examPaper.getId());
    }

    @Override
    @Transactional
    public ExamPaperResponse update(Integer id, ExamPaperEditRequest request) {
        ExamPaper examPaper = examPaperMapper.selectById(id);
        if (examPaper == null) {
            throw new BusinessException("Exam paper not found");
        }
        apply(examPaper, request);
        examPaper.setId(id);
        examPaperMapper.updateByPrimaryKeySelective(examPaper);
        return getById(id);
    }

    @Override
    public Integer count() {
        return examPaperMapper.selectAllCount();
    }

    @Override
    public List<Integer> monthly() {
        List<KeyValue> values = examPaperMapper.selectCountByDate(DateTimeUtil.getMonthStartDay(), DateTimeUtil.getMonthEndDay());
        return DateTimeUtil.MothStartToNowFormat().stream().map(day -> values.stream().filter(v -> v.getName().equals(day)).findFirst().map(KeyValue::getValue).orElse(0)).toList();
    }

    private void apply(ExamPaper examPaper, ExamPaperEditRequest request) {
        examPaper.setName(request.getName());
        examPaper.setSubjectId(request.getSubjectId());
        examPaper.setPaperType(request.getPaperType());
        examPaper.setSuggestTime(request.getSuggestTime());
        examPaper.setLimitStartTime(request.getLimitStartTime());
        examPaper.setLimitEndTime(request.getLimitEndTime());
        examPaper.setGradeLevel(subjectClient.getLevel(request.getSubjectId()));
        List<Integer> ids = request.getTitleItems().stream().flatMap(t -> t.getQuestionItems().stream().map(ExamPaperQuestionItemObject::getId)).distinct().toList();
        List<QuestionClient.QuestionPayload> questions = ids.isEmpty() ? List.of() : questionClient.getByIds(ids);
        examPaper.setQuestionCount(ids.size());
        examPaper.setScore(questions.stream().mapToInt(q -> q.getScore() == null ? 0 : q.getScore()).sum());
        String content = JsonUtil.toJsonStr(request.getTitleItems());
        if (examPaper.getFrameTextContentId() == null) {
            TextContent textContent = new TextContent(content, new Date());
            textContentMapper.insertSelective(textContent);
            examPaper.setFrameTextContentId(textContent.getId());
        } else {
            TextContent textContent = textContentMapper.selectById(examPaper.getFrameTextContentId());
            textContent.setContent(content);
            textContentMapper.updateByPrimaryKeySelective(textContent);
        }
    }

    private ExamPaperResponse toResponse(ExamPaper examPaper) {
        ExamPaperResponse response = new ExamPaperResponse();
        response.setId(examPaper.getId());
        response.setName(examPaper.getName());
        response.setQuestionCount(examPaper.getQuestionCount());
        response.setScore(examPaper.getScore());
        response.setCreateTime(examPaper.getCreateTime());
        response.setCreateUser(examPaper.getCreateUser());
        response.setSubjectId(examPaper.getSubjectId());
        response.setPaperType(examPaper.getPaperType());
        response.setFrameTextContentId(examPaper.getFrameTextContentId());
        response.setSuggestTime(examPaper.getSuggestTime());
        response.setLimitStartTime(examPaper.getLimitStartTime());
        response.setLimitEndTime(examPaper.getLimitEndTime());
        response.setGradeLevel(examPaper.getGradeLevel());
        return response;
    }
}
