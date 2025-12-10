package org.example.helloworld.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传响应")
public class FileUploadVO {
    
    @Schema(description = "文件访问URL", example = "https://example.oss-cn-beijing.aliyuncs.com/xxx.jpg")
    private String url;
    
    @Schema(description = "文件名", example = "avatar.jpg")
    private String fileName;
    
    @Schema(description = "文件大小（字节）", example = "102400")
    private Long size;
}


