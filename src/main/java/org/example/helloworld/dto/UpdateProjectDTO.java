package org.example.helloworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新项目 DTO
 * 用于接收更新项目的请求参数
 * 注意：所有字段都是可选的，只更新传入的字段
 */
@Data
@Schema(description = "更新项目请求")
public class UpdateProjectDTO {

    /**
     * 项目名称（可选）
     */
    @Schema(description = "项目名称", example = "电商平台项目V2")
    private String name;

    /**
     * 项目状态（可选）
     */
    @Schema(description = "项目状态", example = "已完成")
    private String status;

    /**
     * 项目封面（可选）
     */
    @Schema(description = "项目封面URL", example = "https://example.com/new-cover.jpg")
    private String cover;
}

