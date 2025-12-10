package org.example.helloworld.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.core.metadata.IPage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

import org.example.helloworld.dto.CreateProjectDTO;
import org.example.helloworld.dto.ProjectListDTO;
import org.example.helloworld.dto.ProjectResponseDTO;
import org.example.helloworld.dto.UpdateProjectDTO;
import org.example.helloworld.entity.ProjectEntity;
import org.example.helloworld.service.ProjectService;
import org.example.helloworld.utils.BusinessCode;
import org.example.helloworld.utils.Result;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目管理 Controller
 * 
 * 架构说明：
 * 1. 使用 DTO 模式：分离请求参数和实体类
 * 2. 使用 ResponseDTO：统一响应格式，可隐藏敏感字段
 * 3. 参数验证：使用 @Validated 和 Bean Validation 进行参数校验
 * 4. 精确更新：使用 UpdateDTO 只更新传入的字段，避免覆盖其他字段
 * 5. 全局异常处理：使用 GlobalExceptionHandler 统一处理异常
 * 6. RESTful 风格：遵循 REST API 设计规范
 */
@Slf4j
@RestController
@RequestMapping("/project")
@Tag(name = "项目管理", description = "项目相关的增删改查接口")
public class ProjectController {

  @Autowired
  private ProjectService projectService;

  /**
   * 查询项目列表（支持多条件查询和分页）
   * 
   * 支持的查询参数：
   * - name: 项目名称（模糊查询）
   * - status: 项目状态（精确查询）
   * - pageNum: 页码
   * - pageSize: 每页数量
   * - sortField: 排序字段（id, name, status, create_time）
   * - sortOrder: 排序方向（asc, desc）
   * 
   * @param dto 查询条件 DTO
   * @return 项目列表
   */
  @Operation(summary = "查询项目列表", description = "支持多条件查询：按名称、状态查询，支持分页和排序")
  @GetMapping
  public Result projectList(@Validated @ParameterObject ProjectListDTO dto) {
    // 调用 Service 层查询
    IPage<ProjectEntity> projectPage = projectService.projectList(dto);

    // 转换为 ResponseDTO
    List<ProjectResponseDTO> records = projectPage.getRecords().stream()
        .map(ProjectResponseDTO::fromEntity)
        .collect(Collectors.toList());

    return Result.ok()
        .data("total", projectPage.getTotal())
        .data("pages", projectPage.getPages())
        .data("current", projectPage.getCurrent())
        .data("size", projectPage.getSize())
        .data("records", records);
  }

  /**
   * 根据 ID 查询项目详情
   * 
   * @param id 项目 ID
   * @return 项目详情
   */
  @Operation(summary = "查询项目详情", description = "根据项目 ID 查询项目详细信息")
  @GetMapping("/{id}")
  public Result getById(
      @Parameter(description = "项目ID", required = true) @PathVariable @NotNull(message = "项目ID不能为空") @Positive(message = "项目ID必须为正整数") Integer id) {

    ProjectEntity project = projectService.getById(id);
    if (project == null) {
      return Result.fail(BusinessCode.PROJECT_NOT_FOUND).message("项目不存在，ID: " + id);
    }

    // 转换为 ResponseDTO
    ProjectResponseDTO responseDTO = ProjectResponseDTO.fromEntity(project);

    return Result.ok().data("project", responseDTO);
  }

  /**
   * 创建项目
   * 
   * @param dto 创建项目 DTO（自动进行参数验证）
   * @return 创建结果
   */
  @Operation(summary = "创建项目", description = "创建新项目，参数会自动验证")
  @PostMapping
  public Result create(@Valid @RequestBody CreateProjectDTO dto) {
    // Service 层会抛出 BusinessException，由全局异常处理器处理
    ProjectEntity project = projectService.createProject(dto);

    // 转换为 ResponseDTO
    ProjectResponseDTO responseDTO = ProjectResponseDTO.fromEntity(project);

    return Result.ok()
        .message("创建成功")
        .data("project", responseDTO);
  }

  /**
   * 更新项目（部分更新）
   * 
   * 注意：这个接口使用 PATCH 方法，只更新传入的字段
   * 未传入的字段保持不变，避免误覆盖
   * 
   * @param id  项目 ID
   * @param dto 更新项目 DTO（只传需要更新的字段）
   * @return 更新结果
   */
  @Operation(summary = "更新项目（部分更新）", description = "只更新传入的字段，未传入的字段保持不变。推荐使用此接口而不是 PUT。")
  @PatchMapping("/{id}")
  public Result update(
      @Parameter(description = "项目ID", required = true) @PathVariable @NotNull(message = "项目ID不能为空") @Positive(message = "项目ID必须为正整数") Integer id,

      @Valid @RequestBody UpdateProjectDTO dto) {

    // Service 层会处理业务逻辑
    boolean success = projectService.updateProject(id, dto);
    if (!success) {
      return Result.fail(BusinessCode.PROJECT_NOT_FOUND).message("项目不存在，ID: " + id);
    }

    // 返回更新后的项目信息
    ProjectEntity updatedProject = projectService.getById(id);
    ProjectResponseDTO responseDTO = ProjectResponseDTO.fromEntity(updatedProject);

    return Result.ok()
        .message("更新成功")
        .data("project", responseDTO);
  }

  /**
   * 删除项目
   * 
   * @param id 项目 ID
   * @return 删除结果
   */
  @Operation(summary = "删除项目", description = "根据项目 ID 删除项目")
  @DeleteMapping("/{id}")
  public Result delete(
      @Parameter(description = "项目ID", required = true) @PathVariable @NotNull(message = "项目ID不能为空") @Positive(message = "项目ID必须为正整数") Integer id) {

    // 检查项目是否存在
    ProjectEntity project = projectService.getById(id);
    if (project == null) {
      return Result.fail(BusinessCode.PROJECT_NOT_FOUND).message("项目不存在，ID: " + id);
    }

    projectService.removeById(id);
    return Result.ok().message("删除成功");
  }

  /**
   * 批量删除项目
   * 
   * @param ids 项目 ID 列表
   * @return 删除结果
   */
  @Operation(summary = "批量删除项目", description = "根据项目 ID 列表批量删除项目")
  @DeleteMapping("/batch")
  public Result batchDelete(
      @Parameter(description = "项目ID列表", required = true) @RequestBody List<Integer> ids) {

    if (ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("项目 ID 列表不能为空");
    }

    boolean success = projectService.removeByIds(ids);
    if (success) {
      return Result.ok().message("批量删除成功，共删除 " + ids.size() + " 个项目");
    } else {
      return Result.fail(BusinessCode.OPERATION_FAILED).message("批量删除失败");
    }
  }
}
