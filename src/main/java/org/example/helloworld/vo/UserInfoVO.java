package org.example.helloworld.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息响应")
public class UserInfoVO {
    
    @Schema(description = "用户ID", example = "1")
    private Integer id;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
}


