package org.example.helloworld.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新项目 DTO
 * 
 * 更新逻辑说明：
 * 1. null = 不更新该字段（字段未传）
 * 2. "" = 清空该字段（对于可选字段如 cover）
 * 3. 有值 = 更新为该值
 * 
 * 字段保护：
 * - name: 必填字段，不能清空，如果传入必须非空
 * - status: 必填字段，不能清空，如果传入必须非空
 * - cover: 可选字段，允许清空
 */
@Data
@Schema(description = "更新项目请求")
@JsonInclude(JsonInclude.Include.NON_NULL) // 序列化时忽略 null 字段
public class UpdateProjectDTO {

    /**
     * 项目名称（可选更新）
     * 如果传入，不能为空字符串（必填字段保护）
     */
    @Schema(description = "项目名称", example = "电商平台项目V2")
    @Size(min = 1, max = 100, message = "项目名称长度必须在1-100之间")
    private String name;

    /**
     * 项目状态（可选更新）
     * 如果传入，不能为空字符串（必填字段保护）
     */
    @Schema(description = "项目状态", example = "已完成")
    @Size(min = 1, max = 50, message = "项目状态长度必须在1-50之间")
    private String status;

    /**
     * 项目封面（可选更新）
     * 可以传入空字符串来清空封面
     */
    @Schema(description = "项目封面URL，传空字符串可清空", example = "https://example.com/new-cover.jpg")
    private String cover;

    /**
     * 检查字段是否被设置（即使是 null）
     * 用于区分"未传字段"和"传了 null"
     */
    private transient boolean nameSet = false;
    private transient boolean statusSet = false;
    private transient boolean coverSet = false;

    // Setter 方法，用于标记字段已设置
    public void setName(String name) {
        this.name = name;
        this.nameSet = true;
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusSet = true;
    }

    public void setCover(String cover) {
        this.cover = cover;
        this.coverSet = true;
    }

    // 判断字段是否被设置
    public boolean isNameSet() {
        return nameSet;
    }

    public boolean isStatusSet() {
        return statusSet;
    }

    public boolean isCoverSet() {
        return coverSet;
    }

    /**
     * 检查必填字段是否尝试清空
     * 
     * @throws IllegalArgumentException 如果尝试清空必填字段
     */
    public void validateRequiredFields() {
        if (nameSet && (name == null || name.trim().isEmpty())) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
        if (statusSet && (status == null || status.trim().isEmpty())) {
            throw new IllegalArgumentException("项目状态不能为空");
        }
    }
}
