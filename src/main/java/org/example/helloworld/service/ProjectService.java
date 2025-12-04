package org.example.helloworld.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.helloworld.dto.CreateProjectDTO;
import org.example.helloworld.dto.ProjectListDTO;
import org.example.helloworld.dto.UpdateProjectDTO;
import org.example.helloworld.entity.ProjectEntity;

/**
 * 项目服务接口
 * 继承 IService 获得 MyBatis-Plus 提供的 CRUD 方法
 */
public interface ProjectService extends IService<ProjectEntity> {

  IPage<ProjectEntity> projectList(ProjectListDTO dto);

  ProjectEntity createProject(CreateProjectDTO dto);

  boolean updateProject(Integer id, UpdateProjectDTO dto);
}
