package org.example.helloworld.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量文件上传响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量文件上传响应")
public class BatchUploadVO {
    
    @Schema(description = "文件URL列表")
    private List<String> urls;
    
    @Schema(description = "上传文件数量", example = "3")
    private Integer count;
}


