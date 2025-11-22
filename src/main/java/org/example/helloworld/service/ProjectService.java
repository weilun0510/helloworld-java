package org.example.helloworld.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.helloworld.entity.ProjectEntity;

import java.util.List;

/**
 * 订单服务接口
 * 继承 IService 获得 MyBatis-Plus 提供的 CRUD 方法
 */
public interface ProjectService extends IService<ProjectEntity> {

  /**
   * 查询所有订单及其关联的用户
   * 
   * @return 项目列表
   */
  List<ProjectEntity> getAllProjects();

  /**
   * 创建项目
   * 
   * @param project 项目信息
   * @return 创建结果
   */
  boolean createProject(ProjectEntity project);
}
