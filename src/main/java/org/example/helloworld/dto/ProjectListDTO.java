package org.example.helloworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "项目查询请求")
public class ProjectListDTO extends PageDTO {

  /**
   * 项目名称（模糊搜索）
   */
  @Schema(description = "项目名称（模糊搜索）", example = "电商")
  private String name;

  /**
   * 项目状态（精确搜索）
   */
  @Schema(description = "项目状态", example = "进行中", allowableValues = { "进行中", "已完成" })
  private String status;
}
