package org.example.helloworld.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.helloworld.entity.ProjectEntity;
import org.example.helloworld.mapper.ProjectMapper;
import org.example.helloworld.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 订单服务实现类
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

    @Override
    public boolean createProject(ProjectEntity project) {
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new RuntimeException("项目名称不能为空");
        }
        if (project.getStatus() == null) {
            throw new RuntimeException("项目状态不能为空");
        }
        // if (project.getCover() == null || project.getCover().trim().isEmpty()) {
        // throw new RuntimeException("项目封面不能为空");
        // }

        // 自动填充当前时间戳
        project.setCreateTime(LocalDateTime.now());
        return super.save(project);
    }
}
