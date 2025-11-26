package org.example.helloworld.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.helloworld.dto.CreateProjectDTO;
import org.example.helloworld.dto.UpdateProjectDTO;
import org.example.helloworld.entity.ProjectEntity;
import org.example.helloworld.exception.BusinessException;
import org.example.helloworld.exception.ResourceNotFoundException;
import org.example.helloworld.mapper.ProjectMapper;
import org.example.helloworld.service.ProjectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目服务实现类
 * 继承 ServiceImpl 获得 MyBatis-Plus 提供的 CRUD 方法
 * ServiceImpl<Mapper, Entity>
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {

    /**
     * 查询所有项目
     * 
     * @return 项目列表
     */
    @Override
    public List<ProjectEntity> getAllProjects() {
        return baseMapper.selectList(null);
    }

    /**
     * 创建项目（带参数校验）
     * DTO 已经通过 @Valid 进行了基本校验，这里只需要做业务逻辑
     * 
     * @param dto 创建项目 DTO
     * @return 创建的项目实体
     */
    @Override
    public ProjectEntity createProject(CreateProjectDTO dto) {
        // DTO 转 Entity
        ProjectEntity project = new ProjectEntity();
        project.setName(dto.getName());
        project.setStatus(dto.getStatus());
        project.setCover(dto.getCover());
        project.setCreateTime(LocalDateTime.now()); // 设置创建时间

        // 保存到数据库
        boolean success = super.save(project);
        if (!success) {
            throw new BusinessException("创建项目失败");
        }

        return project;
    }

    /**
     * 更新项目（只更新传入的字段）
     * 使用 LambdaUpdateWrapper 实现精确更新，避免覆盖未传入的字段
     * 
     * 更新逻辑：
     * 1. null = 不更新（字段未传）
     * 2. "" = 清空为空字符串（仅对可选字段如 cover）
     * 3. 有值 = 更新为该值
     * 
     * 字段保护：
     * - name: 必填字段，不允许清空
     * - status: 必填字段，不允许清空
     * - cover: 可选字段，允许清空
     * 
     * @param id  项目 ID
     * @param dto 更新项目 DTO
     * @return 是否更新成功
     */
    @Override
    public boolean updateProject(Integer id, UpdateProjectDTO dto) {
        // 检查项目是否存在
        ProjectEntity existingProject = baseMapper.selectById(id);
        if (existingProject == null) {
            throw new ResourceNotFoundException("项目", "ID", id);
        }

        // 验证必填字段不能清空
        dto.validateRequiredFields();

        // 使用 LambdaUpdateWrapper 只更新已设置的字段
        LambdaUpdateWrapper<ProjectEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectEntity::getId, id);

        boolean hasUpdate = false;

        // 1. 更新 name（必填字段，只有传入且非空时才更新）
        if (dto.isNameSet() && dto.getName() != null && !dto.getName().trim().isEmpty()) {
            updateWrapper.set(ProjectEntity::getName, dto.getName().trim());
            hasUpdate = true;
        }

        // 2. 更新 status（必填字段，只有传入且非空时才更新）
        if (dto.isStatusSet() && dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            updateWrapper.set(ProjectEntity::getStatus, dto.getStatus().trim());
            hasUpdate = true;
        }

        // 3. 更新 cover（可选字段，允许清空）
        if (dto.isCoverSet()) {
            // 传入了 cover 字段
            if (dto.getCover() == null || dto.getCover().isEmpty()) {
                // null 或 "" 都表示清空
                updateWrapper.set(ProjectEntity::getCover, null);
            } else {
                // 更新为新值
                updateWrapper.set(ProjectEntity::getCover, dto.getCover().trim());
            }
            hasUpdate = true;
        }

        // 如果没有任何字段需要更新，直接返回 true
        if (!hasUpdate) {
            return true;
        }

        // 执行更新
        return this.update(updateWrapper);
    }
}
