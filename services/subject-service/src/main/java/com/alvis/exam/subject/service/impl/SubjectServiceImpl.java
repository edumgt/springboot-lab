package com.alvis.exam.subject.service.impl;

import com.alvis.exam.common.exception.BusinessException;
import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.subject.domain.Subject;
import com.alvis.exam.subject.dto.SubjectEditRequest;
import com.alvis.exam.subject.dto.SubjectResponse;
import com.alvis.exam.subject.repository.SubjectMapper;
import com.alvis.exam.subject.service.SubjectService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectMapper subjectMapper;

    public SubjectServiceImpl(SubjectMapper subjectMapper) {
        this.subjectMapper = subjectMapper;
    }

    @Override
    public RestPage<SubjectResponse> page(Integer id, Integer level, Integer pageIndex, Integer pageSize) {
        PageInfo<Subject> pageInfo = PageHelper.startPage(pageIndex, pageSize, "id desc")
                .doSelectPageInfo(() -> subjectMapper.page(id, level));
        return new RestPage<>(pageInfo.getList().stream().map(this::toResponse).toList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages());
    }

    @Override
    public List<SubjectResponse> findAll() {
        return subjectMapper.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public SubjectResponse getById(Integer id) {
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new BusinessException("Subject not found");
        }
        return toResponse(subject);
    }

    @Override
    public Integer level(Integer id) {
        return getById(id).getLevel();
    }

    @Override
    public SubjectResponse create(SubjectEditRequest request) {
        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setLevel(request.getLevel());
        subject.setLevelName(request.getLevelName());
        subjectMapper.insertSelective(subject);
        return toResponse(subject);
    }

    @Override
    public SubjectResponse update(Integer id, SubjectEditRequest request) {
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new BusinessException("Subject not found");
        }
        subject.setId(id);
        subject.setName(request.getName());
        subject.setLevel(request.getLevel());
        subject.setLevelName(request.getLevelName());
        subjectMapper.updateByPrimaryKeySelective(subject);
        return toResponse(subjectMapper.selectById(id));
    }

    private SubjectResponse toResponse(Subject subject) {
        SubjectResponse response = new SubjectResponse();
        response.setId(subject.getId());
        response.setName(subject.getName());
        response.setLevel(subject.getLevel());
        response.setLevelName(subject.getLevelName());
        return response;
    }
}
