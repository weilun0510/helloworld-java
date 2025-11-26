package org.example.helloworld.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目响应 DTO
 * 用于返回项目信息给前端
 * 可以根据需要隐藏敏感字段，或添加计算字段
 */
@Data
@Schema(description = "项目响应数据")
public class ProjectResponseDTO {

  /**
   * 项目 ID
   */
  @Schema(description = "项目ID", example = "1")
  private Integer id;

  /**
   * 项目名称
   */
  @Schema(description = "项目名称", example = "电商平台项目")
  private String name;

  /**
   * 项目状态
   */
  @Schema(description = "项目状态", example = "进行中")
  private String status;

  /**
   * 项目封面
   */
  @Schema(description = "项目封面URL", example = "https://example.com/cover.jpg")
  private String cover;

  /**
   * 创建时间
   */
  @Schema(description = "创建时间", example = "2024-11-24 14:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;

  /**
   * 从 Entity 转换为 ResponseDTO
   * 
   * @param entity 项目实体
   * @return 项目响应 DTO
   */
  public static ProjectResponseDTO fromEntity(org.example.helloworld.entity.ProjectEntity entity) {
    if (entity == null) {
      return null;
    }

    ProjectResponseDTO dto = new ProjectResponseDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setStatus(entity.getStatus());
    dto.setCover(entity.getCover());
    dto.setCreateTime(entity.getCreateTime());

    return dto;
  }
}
