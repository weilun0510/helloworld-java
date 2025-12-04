package org.example.helloworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询 DTO 基类
 * 所有需要分页和排序的查询 DTO 都可以继承此类
 */
@Data
@Schema(description = "分页查询基类")
public class PageDTO {

    /**
     * 当前页码（分页查询用）
     */
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum = 1;

    /**
     * 每页大小（分页查询用）
     */
    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    @Min(value = 1, message = "每页数量必须大于等于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "create_time")
    private String sortField;

    /**
     * 排序方向
     */
    @Schema(description = "排序方向", example = "desc", allowableValues = { "asc", "desc" }, defaultValue = "desc")
    private String sortOrder = "desc";
}
