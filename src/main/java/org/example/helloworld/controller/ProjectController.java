package org.example.helloworld.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.example.helloworld.entity.ProjectEntity;
import org.example.helloworld.service.ProjectService;
import org.example.helloworld.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/project")
public class ProjectController {

  @Autowired
  private ProjectService projectService;

  /**
   * 查询所有项目
   * 
   * @return 项目列表
   */
  @Operation(summary = "查询所有项目", description = "查询所有项目")
  @GetMapping("/findAll")
  public Result findAll(@RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
    Page<ProjectEntity> pageParam = new Page<>(page, pageSize);
    IPage<ProjectEntity> projectPage = projectService.page(pageParam);
    return Result.ok()
        .data("total", projectPage.getTotal())
        .data("pages", projectPage.getPages())
        .data("records", projectPage.getRecords());
  }
}
