package org.example.helloworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
 * - cover: 可选字段，允许清空；如果未传则不更新，传空字符串则清空
 */
@Data
@Schema(description = "更新项目请求")
public class UpdateProjectDTO {

    /**
     * 项目名称
     * 如果传入，不能为空字符串（必填字段保护）
     */
    @Schema(description = "项目名称", example = "电商平台项目", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目名称不能为空")
    private String name;

    /**
     * 项目状态
     * 如果传入，不能为空字符串（必填字段保护）
     */
    @Schema(description = "项目状态", example = "进行中", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目状态不能为空")
    private String status;

    /**
     * 项目封面（可选更新）
     * 
     * 更新规则：
     * - 如果字段不存在（未传）：不更新 cover，保持原值
     * - 如果传了 null：不更新 cover，保持原值（JSON 中字段不存在和 null 等价）
     * - 如果传了空字符串 ""：清空 cover（设置为 null）
     * - 如果传了值：更新 cover 为该值
     * 
     * 注意：在 JSON 中，字段不存在和字段值为 null 无法区分，都会被当作"不更新"处理。
     * 如果需要清空 cover，请传空字符串 ""，而不是 null。
     */
    @Schema(description = "项目封面URL。不传或传null则不更新，传空字符串\"\"可清空，传值则更新", example = "https://example.com/new-cover.jpg")
    private String cover;

    /**
     * 标记 cover 字段是否被设置（用于区分"未传字段"和"传了值"）
     * 使用 transient 避免序列化
     */
    private transient boolean coverSet = false;

    /**
     * 自定义 setter，用于标记 cover 字段是否被设置
     * 
     * 注意：在 JSON 反序列化时：
     * - 如果字段不存在：不会调用此 setter，coverSet 保持 false
     * - 如果字段值为 null：会调用 setCover(null)，coverSet 会被设置为 true
     * - 如果字段值为空字符串 ""：会调用 setCover("")，coverSet 会被设置为 true
     * - 如果字段有值：会调用 setCover(value)，coverSet 会被设置为 true
     * 
     * @param cover 封面URL
     */
    public void setCover(String cover) {
        this.cover = cover;
        this.coverSet = true; // 标记字段已被设置（包括 null、空字符串和有效值）
    }

    /**
     * 判断 cover 字段是否被设置
     * 
     * @return true 如果 cover 字段在 JSON 中存在（包括 null、空字符串和有效值），false 如果字段不存在
     */
    public boolean isCoverSet() {
        return coverSet;
    }
}
