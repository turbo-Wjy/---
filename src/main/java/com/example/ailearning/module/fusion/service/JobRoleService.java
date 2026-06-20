package com.example.ailearning.module.fusion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.base.entity.Major;
import com.example.ailearning.module.base.mapper.MajorMapper;
import com.example.ailearning.module.fusion.dto.JobCapabilityRequest;
import com.example.ailearning.module.fusion.dto.JobRoleRequest;
import com.example.ailearning.module.fusion.entity.JobCapability;
import com.example.ailearning.module.fusion.entity.JobRole;
import com.example.ailearning.module.fusion.mapper.JobCapabilityMapper;
import com.example.ailearning.module.fusion.mapper.JobRoleMapper;
import com.example.ailearning.module.fusion.vo.JobCapabilityVO;
import com.example.ailearning.module.fusion.vo.JobRoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobRoleService {
    private final JobRoleMapper jobRoleMapper;
    private final JobCapabilityMapper capabilityMapper;
    private final MajorMapper majorMapper;
    private final JsonValueService jsonValueService;

    public JobRoleService(
            JobRoleMapper jobRoleMapper,
            JobCapabilityMapper capabilityMapper,
            MajorMapper majorMapper,
            JsonValueService jsonValueService
    ) {
        this.jobRoleMapper = jobRoleMapper;
        this.capabilityMapper = capabilityMapper;
        this.majorMapper = majorMapper;
        this.jsonValueService = jsonValueService;
    }

    public PageResult<JobRoleVO> page(PageQuery query, Long majorId) {
        Page<JobRole> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<JobRole> wrapper = new LambdaQueryWrapper<JobRole>()
                .isNull(JobRole::getDeletedAt)
                .orderByAsc(JobRole::getSortOrder)
                .orderByAsc(JobRole::getId);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(JobRole::getRoleCode, query.getKeyword()).or().like(JobRole::getRoleName, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(JobRole::getStatus, query.getStatus());
        }
        if (majorId != null) {
            wrapper.eq(JobRole::getMajorId, majorId);
        }
        Page<JobRole> result = jobRoleMapper.selectPage(page, wrapper);
        List<JobRoleVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public JobRoleVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public JobRoleVO create(JobRoleRequest request) {
        validateMajor(request.getMajorId());
        boolean exists = jobRoleMapper.exists(new LambdaQueryWrapper<JobRole>()
                .eq(JobRole::getRoleCode, request.getRoleCode())
                .isNull(JobRole::getDeletedAt));
        if (exists) {
            throw new BusinessException(ErrorCode.CONFLICT, "岗位能力模型编码已存在");
        }
        JobRole jobRole = new JobRole();
        fill(jobRole, request);
        jobRole.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        jobRoleMapper.insert(jobRole);
        return toVO(jobRole);
    }

    @Transactional(rollbackFor = Exception.class)
    public JobRoleVO update(Long id, JobRoleRequest request) {
        JobRole jobRole = getEntity(id);
        validateMajor(request.getMajorId());
        fill(jobRole, request);
        jobRoleMapper.updateById(jobRole);
        return toVO(jobRole);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        JobRole jobRole = getEntity(id);
        jobRole.setDeletedAt(DeleteConstants.now());
        jobRole.setStatus("deleted");
        jobRoleMapper.updateById(jobRole);
    }

    public List<JobCapabilityVO> listCapabilities(Long jobRoleId) {
        getEntity(jobRoleId);
        return capabilityMapper.selectList(new LambdaQueryWrapper<JobCapability>()
                        .eq(JobCapability::getJobRoleId, jobRoleId)
                        .isNull(JobCapability::getDeletedAt)
                        .orderByAsc(JobCapability::getSortOrder)
                        .orderByAsc(JobCapability::getId))
                .stream().map(this::toCapabilityVO).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public JobCapabilityVO createCapability(Long jobRoleId, JobCapabilityRequest request) {
        getEntity(jobRoleId);
        validateParentCapability(jobRoleId, request.getParentId());
        boolean exists = capabilityMapper.exists(new LambdaQueryWrapper<JobCapability>()
                .eq(JobCapability::getJobRoleId, jobRoleId)
                .eq(JobCapability::getCapabilityCode, request.getCapabilityCode())
                .isNull(JobCapability::getDeletedAt));
        if (exists) {
            throw new BusinessException(ErrorCode.CONFLICT, "岗位能力点编码已存在");
        }
        JobCapability capability = new JobCapability();
        fillCapability(capability, request);
        capability.setJobRoleId(jobRoleId);
        capability.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        capabilityMapper.insert(capability);
        return toCapabilityVO(capability);
    }

    @Transactional(rollbackFor = Exception.class)
    public JobCapabilityVO updateCapability(Long id, JobCapabilityRequest request) {
        JobCapability capability = getCapabilityEntity(id);
        validateParentCapability(capability.getJobRoleId(), request.getParentId());
        fillCapability(capability, request);
        capabilityMapper.updateById(capability);
        return toCapabilityVO(capability);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDeleteCapability(Long id) {
        JobCapability capability = getCapabilityEntity(id);
        capability.setDeletedAt(DeleteConstants.now());
        capability.setStatus("deleted");
        capabilityMapper.updateById(capability);
    }

    public JobRole getEntity(Long id) {
        JobRole jobRole = jobRoleMapper.selectById(id);
        if (jobRole == null || jobRole.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "岗位能力模型不存在");
        }
        return jobRole;
    }

    public JobCapability getCapabilityEntity(Long id) {
        JobCapability capability = capabilityMapper.selectById(id);
        if (capability == null || capability.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "岗位能力点不存在");
        }
        return capability;
    }

    JobRoleVO toVO(JobRole jobRole) {
        JobRoleVO vo = new JobRoleVO();
        vo.setId(jobRole.getId());
        vo.setMajorId(jobRole.getMajorId());
        vo.setRoleCode(jobRole.getRoleCode());
        vo.setRoleName(jobRole.getRoleName());
        vo.setDescription(jobRole.getDescription());
        vo.setTypicalTasks(jobRole.getTypicalTasksJson());
        vo.setAbilityTags(jobRole.getAbilityTagsJson());
        vo.setSortOrder(jobRole.getSortOrder());
        vo.setStatus(jobRole.getStatus());
        return vo;
    }

    JobCapabilityVO toCapabilityVO(JobCapability capability) {
        JobCapabilityVO vo = new JobCapabilityVO();
        vo.setId(capability.getId());
        vo.setJobRoleId(capability.getJobRoleId());
        vo.setParentId(capability.getParentId());
        vo.setCapabilityCode(capability.getCapabilityCode());
        vo.setCapabilityName(capability.getCapabilityName());
        vo.setDescription(capability.getDescription());
        vo.setLevel(capability.getLevel());
        vo.setWeight(capability.getWeight());
        vo.setSortOrder(capability.getSortOrder());
        vo.setStatus(capability.getStatus());
        return vo;
    }

    private void fill(JobRole jobRole, JobRoleRequest request) {
        jobRole.setMajorId(request.getMajorId());
        jobRole.setRoleCode(request.getRoleCode());
        jobRole.setRoleName(request.getRoleName());
        jobRole.setDescription(request.getDescription());
        jobRole.setTypicalTasksJson(jsonValueService.toJson(request.getTypicalTasks()));
        jobRole.setAbilityTagsJson(jsonValueService.toJson(request.getAbilityTags()));
        jobRole.setSortOrder(request.getSortOrder());
        jobRole.setStatus(request.getStatus());
    }

    private void fillCapability(JobCapability capability, JobCapabilityRequest request) {
        capability.setParentId(request.getParentId());
        capability.setCapabilityCode(request.getCapabilityCode());
        capability.setCapabilityName(request.getCapabilityName());
        capability.setDescription(request.getDescription());
        capability.setLevel(request.getLevel());
        capability.setWeight(request.getWeight());
        capability.setSortOrder(request.getSortOrder());
        capability.setStatus(request.getStatus());
    }

    private void validateMajor(Long majorId) {
        if (majorId == null) {
            return;
        }
        Major major = majorMapper.selectById(majorId);
        if (major == null || major.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "专业不存在");
        }
    }

    private void validateParentCapability(Long jobRoleId, Long parentId) {
        if (parentId == null) {
            return;
        }
        JobCapability parent = getCapabilityEntity(parentId);
        if (!jobRoleId.equals(parent.getJobRoleId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "父级能力点必须属于同一岗位模型");
        }
    }
}
