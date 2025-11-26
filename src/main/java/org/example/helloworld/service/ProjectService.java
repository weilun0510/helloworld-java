package org.example.helloworld.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.helloworld.dto.CreateProjectDTO;
import org.example.helloworld.dto.UpdateProjectDTO;
import org.example.helloworld.entity.ProjectEntity;

import java.util.List;

/**
 * 项目服务接口
 * 继承 IService 获得 MyBatis-Plus 提供的 CRUD 方法
 */
public interface ProjectService extends IService<ProjectEntity> {

  /**
   * 查询所有项目
   * 
   * @return 项目列表
   */
  List<ProjectEntity> getAllProjects();

  /**
   * 创建项目（带参数校验）
   * 
   * @param dto 创建项目 DTO
   * @return 创建的项目实体
   */
  ProjectEntity createProject(CreateProjectDTO dto);

  /**
   * 更新项目（只更新传入的字段）
   * 
   * @param id  项目 ID
   * @param dto 更新项目 DTO
   * @return 是否更新成功
   */
  boolean updateProject(Integer id, UpdateProjectDTO dto);
}
