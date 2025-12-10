package org.example.helloworld.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应 VO
 * 
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页响应")
public class PageVO<T> {
    
    @Schema(description = "总记录数", example = "100")
    private Long total;
    
    @Schema(description = "总页数", example = "10")
    private Long pages;
    
    @Schema(description = "当前页码", example = "1")
    private Long current;
    
    @Schema(description = "每页大小", example = "10")
    private Long size;
    
    @Schema(description = "数据列表")
    private List<T> records;
}


