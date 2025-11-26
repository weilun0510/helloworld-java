package org.example.helloworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建项目 DTO
 * 用于接收创建项目的请求参数
 */
@Data
@Schema(description = "创建项目请求")
public class CreateProjectDTO {

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", example = "电商平台项目", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目名称不能为空")
    private String name;

    /**
     * 项目状态
     */
    @Schema(description = "项目状态", example = "进行中", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目状态不能为空")
    private String status;

    /**
     * 项目封面
     */
    @Schema(description = "项目封面URL", example = "https://example.com/cover.jpg")
    private String cover;
}

